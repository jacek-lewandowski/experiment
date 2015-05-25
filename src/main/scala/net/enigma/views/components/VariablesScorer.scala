package net.enigma.views.components

import scala.util.Try

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

  def getVariables(validate: Boolean = true): List[Variable] = {
    val components = for (i ← 0 until layout.getComponentCount)
      yield layout.getComponent(i)

    components.collect {
      case p: VariableComponent ⇒ p.getData match {
        case variable: Variable ⇒
          variable.withScore(p.value(validate))
      }
    }.toList
  }

  private def newComponent(variable: Variable): VariableComponent = {
    new VariableComponent(variable).withSizeFull
  }

  private def valueChanged(): Unit = {
    Try(notifyListeners(getVariables(validate = false).flatMap(_.score).sum))
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
        .withBlurListener { _ ⇒
      Try(textField.validate())
      valueChanged()
    }

    val layout = new HorizontalLayout(new Label(TextResources.Labels.Rank).withSizeUndefined, textField)
        .withWidth("100%")
        .withMargins
        .withSpacing
        .withExpandRatio(textField, 1)

    setContent(layout)

    def value(validate: Boolean): Int = {
      if (validate) textField.validate()
      textField.convertedValue[Integer]
    }
  }

}
