package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification
import org.slf4j.LoggerFactory

import net.enigma.{TextResources, App}
import net.enigma.model.TrialAnswer.TrialAnswerType
import net.enigma.model.{Variable, VariableValue}
import net.enigma.service.TrialStageService
import net.enigma.views.TrialView

/**
 * @author Jacek Lewandowski
 */
trait TrialPresenter extends FlowPresenter {
  self: TrialView ⇒

  private val logger = LoggerFactory.getLogger(classOf[TrialPresenter])

  def stageService: TrialStageService

  val trialSetup = stageService.trialSetup

  override def computeNextValue(selectedVariable: Variable): VariableValue =
    stageService.selectVariable(selectedVariable)

  override def updateSelectionCount(selectionCount: Int): Unit = {
    import trialSetup._
    if (selectionCount >= maxSelectedVariablesCount) {
      grid.disable()
    }
    if (selectionCount >= minSelectedVariablesCount) {
      decisionSelector.setEnabled(true)
      score.setValue((totalScore - (selectionCount - minSelectedVariablesCount) * unitPrice).toString)
    }
  }

  override def selectAnswer(answer: TrialAnswerType): Unit = {
    grid.disable()
    stageService.setAnswer(answer)
    decisionSelector.resetSelection()
    decisionSelector.setEnabled(false)
  }

  override def accept(): Boolean = {
    if (stageService.isAnswerProvided) {
      true
    } else {
      Notification.show(
        TextResources.Notifications.NeedToSelectAnswer,
        Notification.Type.HUMANIZED_MESSAGE
      )
      false
    }
  }

  def nextIteration(): Unit = {
    logger.info("Next iteration")
    val variables = stageService.prepareVariables()

    grid.setVariables(variables)
    decisionSelector.resetSelection()
    decisionSelector.setEnabled(false)
    score.setValue(trialSetup.totalScore.toString)
  }

  override def entered(event: ViewChangeEvent): Unit = {
    App.service.setCurrentStage(id)
    nextIteration()
  }

}
