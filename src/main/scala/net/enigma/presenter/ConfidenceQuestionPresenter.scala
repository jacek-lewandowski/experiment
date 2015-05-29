package net.enigma.presenter

import com.vaadin.data.Property
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification

import net.enigma.TextResources
import net.enigma.Utils._
import net.enigma.service.TrialStageService
import net.enigma.views.ConfidenceQuestionView

/**
 * @author Jacek Lewandowski
 */
trait ConfidenceQuestionPresenter extends FlowPresenter {
  self: ConfidenceQuestionView ⇒

  def stageService: TrialStageService

  override def entered(event: ViewChangeEvent): Unit = {
    answerSlider.setRequired(true)
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
