package net.enigma.views.components

import scala.collection.JavaConversions._

import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import com.vaadin.ui._
import org.slf4j.LoggerFactory

import net.enigma.Utils._
import net.enigma.model.{Variable, VariableValue}
import net.enigma.{TextResources, ValueChangedListenable}

/**
 * @author Jacek Lewandowski
 */
class ExperimentGrid(valueResolver: Variable ⇒ VariableValue)
    extends GridLayout with ValueChangedListenable[Int] {

  private val logger = LoggerFactory.getLogger(classOf[ExperimentGrid])

  setSpacing(true)

  def setVariables(variables: List[Variable]): Unit = {
    logger.info(s"Setting variables: $variables")
    removeAllComponents()
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

  def getVariables: IndexedSeq[VariableValue] = {
    val components = for (component ← getState.childData.keySet())
      yield component

    components.collect {
      case p: VariableComponent if p.value ⇒ p.getData match {
        case variable: VariableValue ⇒ variable
      }
    }.toIndexedSeq
  }

  def disable() = {
    logger.info("Disabling")
    val components = for (component ← getState.childData.keySet())
      yield component

    components.collect {
      case p: VariableComponent ⇒ p
    } foreach (_.disable())
  }

  private class CounterListener extends ValueChangeListener {
    override def valueChange(valueChangeEvent: ValueChangeEvent): Unit = {
      logger.info("Counter listener received notification")
      val selectedCount = getVariables.size
      logger.info(s"Selected count is $selectedCount")
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

    val checkbox = new CheckBox(TextResources.Labels.Select)
        .withWidth("100%")
        .withAdditionalStyleName("large")

    val label = new Label()
        .withWidth("100%")

    val layout = new VerticalLayout(checkbox, label).withSizeFull.withSpacing.withMargins
    layout.setExpandRatio(checkbox, 1)
    layout.setExpandRatio(label, 1)

    checkbox.addValueChangeListener(new ValueChangeListener {
      override def valueChange(valueChangeEvent: ValueChangeEvent): Unit = {
        if (valueChangeEvent.getProperty.getValue == true) {
          removeStyleName("not-selected-variable")
          addStyleName("selected-variable")
          checkbox.setReadOnly(true)
          layout.removeComponent(checkbox)
          val variableValue = valueResolver(variable)
          setData(variableValue)
          label.setValue(variableValue.description)
        }
      }
    })

    def disable(): Unit = {
      checkbox.setEnabled(false)
      checkbox.setVisible(false)
    }

    setContent(layout)

    def value = checkbox.getValue
  }

}
