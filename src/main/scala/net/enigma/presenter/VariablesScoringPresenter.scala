package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification

import net.enigma.{TextResources, App}
import net.enigma.model._
import net.enigma.service.VariablesStageService
import net.enigma.views.VariablesScoringView

/**
 * @author Jacek Lewandowski
 */
trait VariablesScoringPresenter extends FlowPresenter {
  self: VariablesScoringView ⇒

  def stageService: VariablesStageService

  def getVariablesForScoring: List[Variable] = {
    stageService.getVariablesForScoring()
  }

  def setScoredVariables(variables: List[Variable]): Boolean = {
    if (variables.flatMap(_.score).sum != 100) {
      Notification.show(TextResources.Notifications.TotalScoreMustBe100, Notification.Type.HUMANIZED_MESSAGE)
      false
    } else {
      stageService.setScoredVariables(variables)
      true
    }
  }

  override def entered(event: ViewChangeEvent): Unit = {
    scorer.setVariables(getVariablesForScoring)
    App.service.setCurrentStage(id)
  }

  def accept(): Boolean = {
    if (setScoredVariables(scorer.getVariables())) {
      App.service.completeStage(id)
      true
    } else {
      false
    }
  }

  override def totalScoreUpdated(totalScore: Int): Unit = {
    score.setValue((100 - totalScore).toString)
  }

}
