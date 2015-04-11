package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent

import net.enigma.TextResources
import net.enigma.Utils._
import net.enigma.service.TrialStageService
import net.enigma.views.OpenQuestionView

/**
 * @author Jacek Lewandowski
 */
trait ConfidenceQuestionPresenter extends FlowPresenter {
  self: OpenQuestionView ⇒

  def stageService: TrialStageService

  override def entered(event: ViewChangeEvent): Unit = {
    answerField
        .withBlurListener(_ ⇒ answerField.validate())
        .withFocusListener(_ ⇒ answerField.selectAll())
        .withConverter[Integer]
        .withConversionError(TextResources.Notifications.ConfidenceValueInvalid)
        .withConvertedValue(50)
        .setRequired(true)
  }

  override def question: String = TextResources.Labels.ConfidenceQuestion

  override def accept(): Boolean = {
    answerField.validate()
    stageService.setConfidence(answerField.convertedValue[Integer])
    stageService.isConfidenceProvided
  }
}
