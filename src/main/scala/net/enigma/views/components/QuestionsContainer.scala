package net.enigma.views.components

import scala.util.{Failure, Try}

import com.vaadin.data.Validator.InvalidValueException
import com.vaadin.ui.{AbstractComponent, Component, Panel, VerticalLayout}

import net.enigma.model.Question

/**
 * @author Jacek Lewandowski
 */
class QuestionsContainer extends Panel {
  val layout = new VerticalLayout()
  private var items: List[SurveyItem] = Nil
  setContent(layout)

  def setQuestions(questions: Seq[Question]): Unit = {
    items = (for (question ← questions) yield SurveyItem(question)).toList

    layout.removeAllComponents()
    layout.addComponents(items.map(_.component): _*)
  }

  def getAnswers(): Seq[(Question, String)] = {
    items.map {
      case item ⇒ (item.question, item.value)
    }
  }

  def validate(): Unit = {
    items.foreach(_.validate())
  }
}
