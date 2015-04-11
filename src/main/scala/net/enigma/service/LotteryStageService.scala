package net.enigma.service

/**
 * @author Jacek Lewandowski
 */
trait LotteryStageService {

  def getLotteryWinChance: Int

  def confidence(): Unit

  def lottery(): Boolean

  def isStageCompleted: Boolean

}
