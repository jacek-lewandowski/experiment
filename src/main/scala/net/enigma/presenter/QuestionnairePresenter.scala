package net.enigma.presenter

import scala.util.{Failure, Success, Try}

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification

import net.enigma.model.{Answer, Question}
import net.enigma.views.SurveyView
import net.enigma.{App, TextResources}

/**
 * @author Jacek Lewandowski
 */
trait QuestionnairePresenter extends FlowPresenter {
  self: SurveyView ⇒

  def getQuestions(): Seq[Question] = App.service.loadQuestionnaireQuestions()

  def saveResults(questionsAndAnswers: Seq[(Question, String)]): Unit = {
    val answers = for ((question, answer) ← questionsAndAnswers) yield Answer(
      question.id,
      question.caption,
      question.required,
      question.validatorName,
      question.validatorParams,
      answer,
      App.currentUser.get.code
    )

    App.service.saveQuestionnaireAnswers(answers)
  }

  def accept(): Boolean = {
    Try(questionsContainer.validate()) match {
      case Success(_) ⇒
        saveResults(questionsContainer.getAnswers())
        App.service.completeStage(id)
        true
      case Failure(t) ⇒
        Notification.show(TextResources.Notifications.ValidationError, Notification.Type.HUMANIZED_MESSAGE)
        false
    }
  }

  override def entered(event: ViewChangeEvent): Unit = {
    questionsContainer.setQuestions(getQuestions())
    App.service.setCurrentStage(id)
  }

}
