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
      false
    }
  }

  override def lotterySelected(): Unit = {
    selector.resetSelection()
    selector.setEnabled(false)

    val lotteryResult = stageService.lottery()
    if (lotteryResult) {
      Notification.show(TextResources.Notifications.WonTheLottery)
    } else {
      Notification.show(TextResources.Notifications.LoseTheLottery)
    }
  }

  override def confidenceSelected(): Unit = {
    selector.resetSelection()
    selector.setEnabled(false)

    stageService.confidence()
  }

  override def question: String =
    TextResources.Instructions.Lottery.format(stageService.getLotteryWinChance)

  override def entered(event: ViewChangeEvent): Unit = {
    App.service.setCurrentStage(id)
  }

}
