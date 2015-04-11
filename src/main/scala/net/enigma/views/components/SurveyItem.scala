package net.enigma.views.components

import scala.util.{Failure, Try}

import com.vaadin.data.Validator
import com.vaadin.data.validator.{IntegerRangeValidator, RegexpValidator, StringLengthValidator}
import com.vaadin.ui._
import org.slf4j.LoggerFactory
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

  case class OpenSurveyItem(question: Question, validators: Seq[Validator]) extends SurveyItem {
    lazy val component = new Panel() {
      val component = new TextField(question.caption, "")
      component.setRequired(question.required)
      component.setValidationVisible(true)
      for (validator ← validators) component.addValidator(validator)

      val layout = new HorizontalLayout(component)
      layout.setMargin(true)
      layout.setSpacing(true)
      setContent(layout)

      def value: String = component.getValue

      def validate(): Unit = component.validate()
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
    val validators = Try(createValidator(question.validatorName, question.validatorParams))
    validators match {
      case Failure(t) ⇒ logger.error("Invalid validator", t)
      case _ ⇒ logger.info("Creating validator: " + question)
    }
    OpenSurveyItem(question, validators.toOption.toSeq)
  }

  private def createValidator(name: String, params: Map[String, String]): Validator = {
    (name, params) match {
      case StringLength(validator) ⇒ validator
      case IntegerRange(validator) ⇒ validator
      case RegExp(validator) ⇒ validator
    }
  }

  private object StringLength {
    def unapply(nameAndParams: (String, Map[String, String])): Option[StringLengthValidator] = {
      val (name, params) = nameAndParams
      if (name == "length") {
        val errorMessage = params("message")
        val min = params.get("min").map(_.toInt: Integer)
        val max = params.get("max").map(_.toInt: Integer)
        val validator = new StringLengthValidator(errorMessage)
        min.foreach(validator.setMinLength)
        max.foreach(validator.setMaxLength)
        Some(validator)
      } else {
        None
      }
    }
  }

  private object IntegerRange {
    def unapply(nameAndParams: (String, Map[String, String])): Option[IntegerRangeValidator] = {
      val (name, params) = nameAndParams
      if (name == "range") {
        val errorMessage = params("message")
        val min = params.get("min").map(_.toInt: Integer).get
        val max = params.get("max").map(_.toInt: Integer).get
        val validator = new IntegerRangeValidator(errorMessage, min, max)
        Some(validator)
      } else {
        None
      }
    }
  }

  private object RegExp {
    def unapply(nameAndParams: (String, Map[String, String])): Option[RegexpValidator] = {
      val (name, params) = nameAndParams
      if (name == "length") {
        val errorMessage = params("message")
        val regExp = params("pattern")
        val validator = new RegexpValidator(regExp, errorMessage)
        Some(validator)
      } else {
        None
      }
    }
  }

}