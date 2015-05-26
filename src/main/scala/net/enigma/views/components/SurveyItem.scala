package net.enigma.views.components

import scala.util.{Failure, Try}

import com.vaadin.data.validator.{IntegerRangeValidator, RegexpValidator, StringLengthValidator}
import com.vaadin.ui._
import org.slf4j.LoggerFactory

import net.enigma.Utils._
import net.enigma.model.Question

/**
 * @author Jacek Lewandowski
 */
sealed trait SurveyItem {
  def question: Question

  def value: String

  def component: Component

  def validate(): Unit
}

object SurveyItem {

  private val logger = LoggerFactory.getLogger(classOf[SurveyItem])

  case class ClosedSurveyItem(question: Question, options: Seq[String]) extends SurveyItem {
    lazy val component = {
      val optionsLayout = new HorizontalLayout()
      optionsLayout.setSpacing(true)

      def layoutCreator(label: Component, item: Component): Component = {
        val layout = new VerticalLayout(label, item)
        layout.setSpacing(true)
        layout.setExpandRatio(label, 1)
        layout.setMargin(true)
        layout
      }

      def itemLayoutCreator(label: Component, item: Component): Component = {
        val layout = new HorizontalLayout(label, item)
        layout.setSpacing(true)
        layout.setExpandRatio(label, 1)
        layout
      }

      new SurveyOptionGroup(
        optionsLayout,
        layoutCreator,
        itemLayoutCreator,
        options.map(opt ⇒ (opt, opt)),
        question.caption,
        question.required
      )
    }

    override def value: String = component.value

    override def validate(): Unit = component.validate()
  }

  case class LikertSurveyItem(question: Question) extends SurveyItem {
    lazy val component = {
      val optionsLayout = new HorizontalLayout()
      optionsLayout.setSpacing(true)

      def layoutCreator(label: Component, item: Component): Component = {
        val layout = new HorizontalLayout(label, item)
        layout.setWidth("100%")
        layout.setSpacing(true)
        layout.setExpandRatio(label, 1)
        layout.setMargin(true)
        layout
      }

      def itemLayoutCreator(label: Component, item: Component): Component = {
        val layout = new VerticalLayout(item, label)
        layout.setSpacing(false)
        layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER)
        layout.setComponentAlignment(item, Alignment.MIDDLE_CENTER)
        layout
      }

      val options = (1 to 5).map(i ⇒ (i.toString, i.toString))

      new SurveyOptionGroup(
        optionsLayout,
        layoutCreator,
        itemLayoutCreator,
        options,
        question.caption,
        question.required
      )
    }

    override def value: String = component.value

    override def validate(): Unit = component.validate()
  }

  case class OpenSurveyItem(question: Question) extends SurveyItem {
    lazy val component = new Panel() {
      val textField = new TextField(question.caption, "")

      textField.withBlurListener(_ ⇒ Try(textField.validate()))
      textField.withFocusListener(_ ⇒ textField.selectAll())
      textField.setRequired(question.required)

      Try(applyValidators(question.validatorName, question.validatorParams, textField)) match {
        case Failure(t) ⇒ logger.error("Invalid validator", t)
        case _ ⇒ logger.info("Created validators")
      }

      val layout = new HorizontalLayout(textField)
      layout.setMargin(true)
      layout.setSpacing(true)
      setContent(layout)

      def value: String = textField.getValue

      def validate(): Unit = textField.validate()
    }

    override def value: String = component.value

    override def validate(): Unit = component.validate()
  }

  def apply(question: Question): SurveyItem = {
    logger.info(question.toString)
    question match {
      case Question(_, _, _, _, "selection", _) ⇒
        createClosedSurveyItem(question)
      case Question(_, _, _, _, "likert", _) ⇒
        createLikertSurveyItem(question)
      case Question(_, _, _, _, _, _) ⇒
        createOpenSurveyItem(question)
    }
  }

  private def createClosedSurveyItem(question: Question): ClosedSurveyItem = {
    logger.info("Create closed question")
    val (_, answers) =
      (for ((key, value) ← question.validatorParams) yield (key.toInt, value))
          .toIndexedSeq.sortBy(_._1).unzip

    ClosedSurveyItem(question, answers)
  }

  private def createLikertSurveyItem(question: Question): LikertSurveyItem = {
    logger.info("Create likert validator")
    LikertSurveyItem(question)
  }

  private def createOpenSurveyItem(question: Question): OpenSurveyItem = {
    OpenSurveyItem(question)
  }

  private def applyValidators(name: String, params: Map[String, String], component: AbstractField[_]) {
    (name, params) match {
      case StringLength(min, max, msg) ⇒
        val validator = new StringLengthValidator(msg)
        min.foreach(x ⇒ validator.setMinLength(x))
        max.foreach(x ⇒ validator.setMaxLength(x))
        component.addValidator(validator)

      case IntegerRange(min, max, msg) ⇒
        component.setConverter(classOf[Integer])
        component.addValidator(new IntegerRangeValidator(msg, min, max))
        component.setConvertedValue(0)

      case RegExp((exp, msg)) ⇒
        component.addValidator(new RegexpValidator(exp, msg))
    }
  }

  private object StringLength {
    def unapply(nameAndParams: (String, Map[String, String])): Option[(Option[Int], Option[Int], String)] = {
      val (name, params) = nameAndParams
      if (name == "length") {
        val errorMessage = params("message")
        val min = params.get("min").map(_.toInt)
        val max = params.get("max").map(_.toInt)
        Some((min, max, errorMessage))
      } else {
        None
      }
    }
  }

  private object IntegerRange {
    def unapply(nameAndParams: (String, Map[String, String])): Option[(Int, Int, String)] = {
      val (name, params) = nameAndParams
      if (name == "range") {
        val errorMessage = params("message")
        val min = params.get("min").map(_.toInt).get
        val max = params.get("max").map(_.toInt).get
        Some((min, max, errorMessage))
      } else {
        None
      }
    }
  }

  private object RegExp {
    def unapply(nameAndParams: (String, Map[String, String])): Option[(String, String)] = {
      val (name, params) = nameAndParams
      if (name == "length") {
        val errorMessage = params("message")
        val regExp = params("pattern")
        Some((regExp, errorMessage))
      } else {
        None
      }
    }
  }

}