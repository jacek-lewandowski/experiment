package net.enigma.views.components

import scala.collection.JavaConversions._

import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import com.vaadin.ui._

import net.enigma.Utils._
import net.enigma.ValueChangedListenable
import net.enigma.model.Variable

/**
 * @author Jacek Lewandowski
 */
class JustificationsGrid extends GridLayout with ValueChangedListenable[Int] {

  setSpacing(true)

  def setVariables(variables: Seq[Variable]): Unit = {
    val (columns, rows) = getGridSize(variables.length)
    setColumns(columns)
    setRows(rows)

    val locations = for (c ← 0 until columns; r ← 0 until rows) yield (c, r)
    for (((c, r), variable) ← locations zip variables) {
      val component = newComponent(variable)
      addComponent(component, c, r)
      component.justification.addValueChangeListener(new CounterListener)
    }
  }

  def getVariables: List[Variable] = {
    val components = for (component ← getState.childData.keySet())
      yield component

    components.collect {
      case p: VariableComponent ⇒ p.getData match {
        case variable: Variable ⇒ variable.copy(justification = p.value)
      }
    }.toList
  }

  private class CounterListener extends ValueChangeListener {
    override def valueChange(valueChangeEvent: ValueChangeEvent): Unit = {
      val selectedCount = getVariables.size
      notifyListeners(selectedCount)
    }
  }

  private def getGridSize(itemsCount: Int): (Int, Int) = {
    val columns = Math.ceil(Math.sqrt(itemsCount)).toInt max 1 min 4
    val rows = Math.ceil(itemsCount.toDouble / columns.toDouble).toInt max 1
    (columns, rows)
  }

  private def newComponent(variable: Variable): VariableComponent = {
    new VariableComponent(variable).withSizeFull
  }

  private class VariableComponent(variable: Variable) extends Panel(variable.title) {
    setData(variable)
    addStyleName("not-selected-variable")

    val justification = new TextArea()
        .withSizeFull
        .withAdditionalStyleName("small")
        .withMaxLength(1000)
    val layout = new VerticalLayout(justification).withSizeFull.withSpacing.withMargins
    layout.setExpandRatio(justification, 1)

    justification.addValueChangeListener(new ValueChangeListener {
      override def valueChange(valueChangeEvent: ValueChangeEvent): Unit = {
        if (Option(valueChangeEvent.getProperty.getValue).getOrElse("").toString.trim.nonEmpty) {
          removeStyleName("not-selected-variable")
          addStyleName("selected-variable")
        } else {
          removeStyleName("selected-variable")
          addStyleName("not-selected-variable")
        }
      }
    })

    setContent(layout)

    def value: Option[String] =
      if (justification.getValue == null || justification.getValue.trim == "") None
      else Some(justification.getValue.trim)
  }

}
