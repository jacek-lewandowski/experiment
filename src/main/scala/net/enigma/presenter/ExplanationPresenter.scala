package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent

import net.enigma.TextResources
import net.enigma.service.TrialStageService
import net.enigma.views.OpenQuestionView

import net.enigma.Utils._

/**
 * @author Jacek Lewandowski
 */
trait ExplanationPresenter extends FlowPresenter {
  self: OpenQuestionView ⇒

  def stageService: TrialStageService

  override def entered(event: ViewChangeEvent): Unit = {
    answerField
        .withBlurListener(_ ⇒ answerField.validate())
        .withFocusListener(_ ⇒ answerField.selectAll())
        .setValue("")
  }

  override def question: String = TextResources.Labels.ExplanationQuestion

  override def accept(): Boolean = {
    stageService.setExplanation(answerField.getValue)
    stageService.isExplanationProvided
  }

}
