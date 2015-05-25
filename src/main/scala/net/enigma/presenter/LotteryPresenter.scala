package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification

import net.enigma.{App, TextResources}
import net.enigma.service.LotteryStageService
import net.enigma.views.LotteryView

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

    val result = stageService.lottery()
    if (result) {
      Notification.show(TextResources.Notifications.WonTheLottery)
    } else {
      Notification.show(TextResources.Notifications.LoseTheLottery)
    }
  }

  override def confidenceSelected(): Unit = {
    selector.resetSelection()
    selector.setEnabled(false)

    val result = stageService.confidence()
    if (result) {
      Notification.show(TextResources.Notifications.CorrectAnswerProvided)
    } else {
      Notification.show(TextResources.Notifications.NoCorrectAnswerProvided)
    }
  }

  override def question: String =
    TextResources.Instructions.LotteryQuestion.format(stageService.getLotteryWinChance)

  override def entered(event: ViewChangeEvent): Unit = {
    App.service.setCurrentStage(id)
  }

}
