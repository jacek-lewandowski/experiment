package net.enigma.views.components

import com.vaadin.data.validator.IntegerRangeValidator
import com.vaadin.ui._

import net.enigma.Utils._
import net.enigma.model.Variable
import net.enigma.{TextResources, ValueChangedListenable}

/**
 * @author Jacek Lewandowski
 */
class VariablesScorer extends Panel with ValueChangedListenable[Int] {

  val layout = new VerticalLayout().withSpacing.withMargins.withSizeUndefined.withWidth("100%")
  setContent(layout)

  def setVariables(variables: Seq[Variable]): Unit = {
    for (variable ← variables) {
      layout.addComponent(newComponent(variable))
    }
  }

  def getVariables: List[Variable] = {
    val components = for (i ← 0 until layout.getComponentCount)
      yield layout.getComponent(i)

    components.collect {
      case p: VariableComponent ⇒ p.getData match {
        case variable: Variable ⇒
          variable.withScore(p.value)
      }
    }.toList
  }

  private def newComponent(variable: Variable): VariableComponent = {
    new VariableComponent(variable).withSizeFull
  }

  private class VariableComponent(variable: Variable) extends Panel(variable.title) {
    setData(variable)

    private val textField = new TextField()
        .withConverter[Integer]
        .withAdditionalValidator(new IntegerRangeValidator(TextResources.Notifications.RankValueOutOfRange, 1, 100))
        .withConvertedValue(0)
        .withMaxLength(2)
        .withConversionError(TextResources.Notifications.RankValueOutOfRange)

    textField
        .withFocusListener(_ ⇒ textField.selectAll())
        .withBlurListener(_ ⇒ textField.validate())

    val layout = new HorizontalLayout(new Label(TextResources.Labels.Rank).withSizeUndefined, textField)
        .withWidth("100%")
        .withMargins
        .withSpacing
        .withExpandRatio(textField, 1)

    setContent(layout)

    def value: Int = {
      textField.validate()
      textField.convertedValue[Integer]
    }
  }

}
