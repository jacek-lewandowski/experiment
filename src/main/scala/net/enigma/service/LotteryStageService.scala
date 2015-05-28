package net.enigma.service

import net.enigma.model.LotteryStageInfo

/**
 * @author Jacek Lewandowski
 */
trait LotteryStageService {

  def getLotteryWinChance: Int

  def confidence(): (Boolean, Int)

  def lottery(): Boolean

  def isStageCompleted: Boolean

  def getLotteryStageInfo(): LotteryStageInfo
}
