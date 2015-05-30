package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification

import net.enigma.TextResources
import net.enigma.service.TrialStageService
import net.enigma.views.ConfidenceQuestionView

/**
 * @author Jacek Lewandowski
 */
trait ConfidenceQuestionPresenter extends FlowPresenter {
  self: ConfidenceQuestionView ⇒

  def stageService: TrialStageService

  override def entered(event: ViewChangeEvent): Unit = {
    if (stageService.isAwaitingConfidence) {
      answerSlider.setRequired(true)
    } else {
      navigateTo(nextView)
    }
  }

  override def question: String = TextResources.Labels.ConfidenceQuestion

  override def accept(): Boolean = {
    answerSlider.validate()
    if (answerSlider.getValue < 50) {
      Notification.show(TextResources.Notifications.ConfidenceValueInvalid)
      false
    } else {
      stageService.setConfidence(answerSlider.getValue.toInt)
      stageService.isConfidenceProvided
    }
  }
}
