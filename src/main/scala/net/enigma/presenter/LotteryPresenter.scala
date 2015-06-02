package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification

import net.enigma.service.LotteryStageService
import net.enigma.views.LotteryView
import net.enigma.{App, TextResources}

/**
 * @author Jacek Lewandowski
 */
trait LotteryPresenter extends FlowPresenter {
  self: LotteryView ⇒

  def stageService: LotteryStageService

  override def accept(): Boolean = {
    if (stageService.isStageCompleted) {
      App.service.completeStage(id)
      true
    } else {
      Notification.show(TextResources.Notifications.MustChooseLotteryOrBet, Notification.Type.HUMANIZED_MESSAGE)
      false
    }
  }

  override def lotterySelected(): Unit = {
    selector.resetSelection()
    selector.setEnabled(false)

    stageService.lottery()
    showLotteryResult()
  }

  override def confidenceSelected(): Unit = {
    selector.resetSelection()
    selector.setEnabled(false)

    stageService.bet()
    showBetResult()
  }

  override def question: String =
    TextResources.Instructions.LotteryQuestion.format(stageService.getLotteryWinChance)

  def showBetResult() = {
    val stageInfo = stageService.getLotteryStageInfo()
    val result = stageInfo.result.get
    val iterIdx = stageInfo.selectedIterIdx
    if (result) {
      Notification.show(TextResources.Notifications.CorrectAnswerProvided.format(iterIdx + 1))
      resultLabel.setValue(TextResources.Notifications.CorrectAnswerProvided.format(iterIdx + 1))
    } else {
      Notification.show(TextResources.Notifications.NoCorrectAnswerProvided.format(iterIdx + 1))
      resultLabel.setValue(TextResources.Notifications.NoCorrectAnswerProvided.format(iterIdx + 1))
    }
  }

  def showLotteryResult() = {
    val stageInfo = stageService.getLotteryStageInfo()
    val result = stageInfo.result.get
    if (result) {
      Notification.show(TextResources.Notifications.WonTheLottery)
      resultLabel.setValue(TextResources.Notifications.WonTheLottery)
    } else {
      Notification.show(TextResources.Notifications.LoseTheLottery)
      resultLabel.setValue(TextResources.Notifications.LoseTheLottery)
    }
  }

  override def entered(event: ViewChangeEvent): Unit = {
    App.service.setCurrentStage(id)
    if (stageService.isStageCompleted) {
      selector.resetSelection()
      selector.setEnabled(false)

      if (stageService.getLotteryStageInfo().lotterySelected) {
        showLotteryResult()
      } else {
        showBetResult()
      }
    } else if (stageService.getLotteryWinChance == 100) {
      stageService.bet()
      App.service.completeStage(id)
      navigateTo(nextView)
    }
  }



}
