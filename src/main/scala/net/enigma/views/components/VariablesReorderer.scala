package net.enigma.views.components

import com.vaadin.ui._

import net.enigma.Utils._
import net.enigma.model.Variable

/**
 * @author Jacek Lewandowski
 */
class VariablesReorderer extends Panel {
  val layout = DragAndDropLayout.vertical().withMargins.withSpacing
  setContent(layout)

  def setVariables(variables: Seq[Variable]): Unit = {
    for (variable ← variables) {
      val component = newComponent(variable)
      layout.addDraggableComponent(component)
    }
  }

  def getVariables: List[Variable] = {
    val components = for (component ← layout.getDraggableComponents)
      yield component

    val orderableVariables = components.collect {
      case p: Panel ⇒ p.getData match {
        case variable: Variable ⇒ variable
      }
    }

    for ((variable, index) ← orderableVariables.zipWithIndex)
      yield variable.withOrdinalNumber(index)
  }

  private def newComponent(variable: Variable): Panel = {
    val component = new Panel(variable.title).withAdditionalStyleName("selected-variable")
    component.setData(variable)
    component
  }

}

