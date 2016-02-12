package net.enigma.service

import net.enigma.model.LotteryStageInfo

/**
 * @author Jacek Lewandowski
 */
trait LotteryStageService {

  def getLotteryWinChance: Int

  def bet(): Unit

  def lottery(): Unit

  def isStageCompleted: Boolean

  def getLotteryStageInfo(): LotteryStageInfo
}
