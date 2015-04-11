package net.enigma.service.impl

import net.enigma.service.LotteryStageService

/**
 * @author Jacek Lewandowski
 */
class LotteryStageServiceImpl(userCode: String) extends LotteryStageService {
  override def getLotteryWinChance: Int = 50

  override def confidence(): Unit = {}

  override def lottery(): Boolean = true

  override def isStageCompleted: Boolean = true
}
