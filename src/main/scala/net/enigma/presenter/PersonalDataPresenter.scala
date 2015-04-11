package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent

import net.enigma.App
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
      App.currentUser.get.code
    )

    App.service.savePersonalDataAnswers(answers)
  }

  def accept(): Boolean = {
    questionsContainer.validate()
    saveResults(questionsContainer.getAnswers())
    App.service.completeStage(id)
    true
  }

  override def entered(event: ViewChangeEvent): Unit = {
    questionsContainer.setQuestions(getQuestions())
    App.service.setCurrentStage(id)
  }

}
