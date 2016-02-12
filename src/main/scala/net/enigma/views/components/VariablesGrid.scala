package net.enigma.views.components

import scala.collection.JavaConversions._

import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import com.vaadin.event.MouseEvents.ClickEvent
import com.vaadin.shared.MouseEventDetails.MouseButton
import com.vaadin.ui.{VerticalLayout, CheckBox, GridLayout, Panel}

import net.enigma.Utils._
import net.enigma.model.Variable
import net.enigma.{TextResources, ValueChangedListenable}

/**
 * @author Jacek Lewandowski
 */
class VariablesGrid extends GridLayout with ValueChangedListenable[Int] {

  setSpacing(true)

  def setVariables(variables: Seq[Variable]): Unit = {
    val (columns, rows) = getGridSize(variables.length)
    setColumns(columns)
    setRows(rows)

    val locations = for (c ← 0 until columns; r ← 0 until rows) yield (c, r)
    for (((c, r), variable) ← locations zip variables) {
      val component = newComponent(variable)
      addComponent(component, c, r)
      component.checkbox.addValueChangeListener(new CounterListener)
    }
  }

  def getVariables: List[Variable] = {
    val components = for (component ← getState.childData.keySet())
      yield component

    components.collect {
      case p: VariableComponent if p.value ⇒ p.getData match {
        case variable: Variable ⇒ variable
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

    this.withClickListener(e => if (e.getButton == MouseButton.LEFT) checkbox.setValue(!checkbox.getValue))

    val checkbox = new CheckBox(TextResources.Labels.Select)
      .withWidth("100%")

    val layout = new VerticalLayout(checkbox).withSizeFull.withSpacing.withMargins
    layout.setExpandRatio(checkbox, 1)

    checkbox.addValueChangeListener(new ValueChangeListener {
      override def valueChange(valueChangeEvent: ValueChangeEvent): Unit = {
        if (valueChangeEvent.getProperty.getValue == true) {
          removeStyleName("not-selected-variable")
          addStyleName("selected-variable")
        } else {
          removeStyleName("selected-variable")
          addStyleName("not-selected-variable")
        }
      }
    })

    setContent(layout)

    def value = checkbox.getValue
  }

}
