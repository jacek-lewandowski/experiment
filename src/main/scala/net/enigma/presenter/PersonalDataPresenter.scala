package net.enigma.presenter

import scala.util.{Failure, Success, Try}

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification

import net.enigma.{TextResources, App}
import net.enigma.model.{Answer, Question}
import net.enigma.views.SurveyView

/**
 * @author Jacek Lewandowski
 */
trait PersonalDataPresenter extends FlowPresenter {
  self: SurveyView ⇒

  def getQuestions(): Seq[Question] = App.service.loadPersonalDataQuestions()

  def saveResults(questionsAndAnswers: Seq[(Question, String)]): Unit = {
    val answers = for ((question, answer) ← questionsAndAnswers) yield Answer(
      question.id,
      question.caption,
      question.required,
      question.validatorName,
      question.validatorParams,
      answer,
      App.currentUser.get
    )

    App.service.savePersonalDataAnswers(answers)
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
