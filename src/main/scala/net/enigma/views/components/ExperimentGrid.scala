package net.enigma.views.components

import scala.collection.JavaConversions._

import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import com.vaadin.shared.MouseEventDetails.MouseButton
import com.vaadin.ui._
import org.slf4j.LoggerFactory

import net.enigma.Utils._
import net.enigma.model.TrialAnswer.TrialAnswerType
import net.enigma.model.{Variable, VariableValue}
import net.enigma.{TextResources, ValueChangedListenable}

/**
 * @author Jacek Lewandowski
 */
class ExperimentGrid(valueResolver: Variable ⇒ VariableValue)
    extends GridLayout with ValueChangedListenable[Int] {

  private val logger = LoggerFactory.getLogger(classOf[ExperimentGrid])

  setSpacing(true)

  def setVariables(variables: List[Variable], resolved: List[VariableValue]): Unit = {
    logger.info(s"Setting variables: $variables")
    removeAllComponents()
    val (columns, rows) = getGridSize(variables.length)
    setColumns(columns)
    setRows(rows)

    val resolvedMap = resolved.map(vv ⇒ (vv.variable.id, vv)).toMap
    val variablesMap = variables.map(v ⇒ (v, resolvedMap.get(v.id)))

    val locations = for (c ← 0 until columns; r ← 0 until rows) yield (c, r)
    for (((c, r), (variable, optValue)) ← locations zip variablesMap) {
      val component = newComponent(variable, optValue.map(vv ⇒ (vv.value, vv.description)))
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

  private def newComponent(variable: Variable, varValue: Option[(TrialAnswerType, String)]): VariableComponent = {
    new VariableComponent(variable, varValue).withSizeFull
  }

  private class VariableComponent(variable: Variable, varValue: Option[(TrialAnswerType, String)])
      extends Panel(variable.title) {

    setData(variable)

    this.withClickListener(e =>
      if (e.getButton == MouseButton.LEFT && !checkbox.isReadOnly && checkbox.isEnabled  && checkbox.isVisible)
        checkbox.setValue(!checkbox.getValue))

    val checkbox = new CheckBox(TextResources.Labels.Select)
        .withWidth("100%")

    val label = new Label()
        .withWidth("100%")

    val layout = new VerticalLayout(checkbox, label).withSizeFull.withSpacing.withMargins
    layout.setExpandRatio(checkbox, 1)
    layout.setExpandRatio(label, 1)

    varValue match {
      case Some((trialAnswer, description)) ⇒
        addStyleName("selected-variable")
        checkbox.setReadOnly(true)
        layout.removeComponent(checkbox)
        setData(VariableValue(variable, trialAnswer, description))
        label.setValue(description)

      case _ ⇒
        addStyleName("not-selected-variable")
    }

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
