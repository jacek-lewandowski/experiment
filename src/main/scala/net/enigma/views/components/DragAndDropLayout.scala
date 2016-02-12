package net.enigma.views.components

import com.vaadin.event.dd.acceptcriteria.{AcceptCriterion, Not, SourceIsTarget}
import com.vaadin.event.dd.{DragAndDropEvent, DropHandler}
import com.vaadin.shared.ui.dd.{HorizontalDropLocation, VerticalDropLocation}
import com.vaadin.ui.DragAndDropWrapper.DragStartMode
import com.vaadin.ui._

/**
 * @author Jacek Lewandowski
 */

trait DragAndDropLayout {
  self: AbstractOrderedLayout ⇒

  lazy val dropHandler: DropHandler = new DragAndDropLayout.DropHandlerImpl(this)

  def addDraggableComponent(component: Component): Unit = {
    val wrappedComponent = new DragAndDropLayout.WrappedComponent(component, dropHandler)
    addComponent(wrappedComponent)
  }

  def getDraggableComponents: List[Component] = {
    val components = for (i ← 0 until getComponentCount) yield getComponent(i)
    components.collect {
      case c: DragAndDropLayout.WrappedComponent ⇒ c.content
    }.toList
  }
}


object DragAndDropLayout {

  def vertical(): VerticalLayout with DragAndDropLayout = new VerticalDragAndDropLayout

  def horizontal(): HorizontalLayout with DragAndDropLayout = new HorizontalDragAndDropLayout

  private class VerticalDragAndDropLayout extends VerticalLayout with DragAndDropLayout

  private class HorizontalDragAndDropLayout extends HorizontalLayout with DragAndDropLayout

  private class DropHandlerImpl(val layout: AbstractOrderedLayout) extends DropHandler {

    override def drop(dropEvent: DragAndDropEvent) {
      val transferable = dropEvent.getTransferable
      val sourceComponent = transferable.getSourceComponent

      sourceComponent match {
        case _: WrappedComponent ⇒
          val dropTargetData = dropEvent.getTargetDetails
          val target = dropTargetData.getTarget

          // find the location where to move the dragged component
          var sourceWasAfterTarget = true
          var index = 0
          val componentIterator = layout.iterator()
          var next: Component = null
          while (next != target && componentIterator.hasNext) {
            next = componentIterator.next()
            if (next != sourceComponent) {
              index += 1
            } else {
              sourceWasAfterTarget = false
            }
          }

          if (next != null && next == target) {
            layout match {
              case _: HorizontalLayout ⇒
                if (dropTargetData.getData("horizontalLocation") == HorizontalDropLocation.CENTER.toString) {
                  // drop on top of target?
                  if (sourceWasAfterTarget) index -= 1
                } else if (dropTargetData.getData("horizontalLocation") == HorizontalDropLocation.LEFT.toString) {
                  // drop before the target?
                  index -= 1
                  if (index < 0) index = 0
                }
              case _: VerticalLayout ⇒
                if (dropTargetData.getData("verticalLocation") == VerticalDropLocation.MIDDLE.toString) {
                  // drop on top of target?
                  if (sourceWasAfterTarget) index -= 1
                } else if (dropTargetData.getData("verticalLocation") == VerticalDropLocation.TOP.toString) {
                  // drop before the target?
                  index -= 1
                  if (index < 0) index = 0
                }
            }

            // move component within the layout
            layout.removeComponent(sourceComponent)
            layout.addComponent(sourceComponent, index)
          }
      }
    }

    override def getAcceptCriterion: AcceptCriterion = new Not(SourceIsTarget.get())

  }

  private class WrappedComponent(val content: Component, val dropHandler: DropHandler)
      extends DragAndDropWrapper(content) {

    setDragStartMode(DragStartMode.WRAPPER)

    override def getDropHandler: DropHandler = dropHandler
  }

}
