package net.enigma.views.components

import scala.util.{Failure, Try}

import com.vaadin.data.Validator.InvalidValueException
import com.vaadin.ui.{Component, Panel, VerticalLayout}

import net.enigma.model.Question

/**
 * @author Jacek Lewandowski
 */
class QuestionsContainer extends Panel {
  val layout = new VerticalLayout()
  setContent(layout)

  def setQuestions(questions: Seq[Question]): Unit = {
    val items = for (question ← questions)
      yield SurveyItem(question).component

    layout.removeAllComponents()
    layout.addComponents(items.toSeq: _*)
  }

  def getAnswers(): Seq[(Question, String)] = {
    val components = for (i ← 0 until layout.getComponentCount) yield layout.getComponent(i)
    components.collect {
      case item: SurveyItem ⇒ (item.question, item.value)
    }
  }

  def validate(): Unit = {
    val components = for (i ← 0 until layout.getComponentCount) yield layout.getComponent(i)
    val validationResults = components.collect {
      case item: SurveyItem ⇒ Try(item.validate())
    }
    val failureCauses = for (Failure(cause) ← validationResults) yield cause
    if (failureCauses.nonEmpty)
      throw new InvalidValueException("Multiple validation failures",
        failureCauses.collect { case ex: InvalidValueException ⇒ ex }: _*)
  }
}
