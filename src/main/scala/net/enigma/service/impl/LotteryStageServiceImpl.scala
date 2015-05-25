package net.enigma.service.impl

import org.json4s.native.Serialization.{read, write}
import org.scalactic.Requirements._

import net.enigma.App
import net.enigma.db.StageDataDAO
import net.enigma.db.StageDataDAO.Lottery
import net.enigma.model.{Iteration, LotteryStageInfo, StageData}
import net.enigma.service.LotteryStageService

/**
 * @author Jacek Lewandowski
 */
class LotteryStageServiceImpl(userCode: String, lastIterationsCount: Int) extends LotteryStageService {

  lazy val trialStageService = App.service.getTrialStageService

  import StageDataDAO.Lottery.formats

  def saveLotteryInfo(json: String): Unit = {
    StageDataDAO.saveStageData(StageData(userCode, Lottery.stageID, Lottery.stageInfoID, 0, json))
  }

  def saveLotteryInfo(info: LotteryStageInfo): Unit = {
    val json = write(info)
    saveLotteryInfo(json)
  }

  def getLastIterations(): List[Iteration] = {
    trialStageService.getIterations(-lastIterationsCount, lastIterationsCount)
  }

  override def getLotteryWinChance: Int = {
    val info = getLotteryStageInfo()
    info.winChance
  }

  override def confidence(): Boolean = {
    val info = getLotteryStageInfo()
    requireState(info.result.isEmpty)
    val result = getLastIterations().flatMap(_.isAnswerCorrect).contains(true)
    saveLotteryInfo(info.copy(lotterySelected = false, result = Some(result)))
    result
  }

  override def lottery(): Boolean = {
    val info = getLotteryStageInfo()
    requireState(info.result.isEmpty)
    val result = App.random.nextInt(100) < info.winChance
    saveLotteryInfo(info.copy(lotterySelected = true, result = Some(result)))
    result
  }

  override def isStageCompleted: Boolean = {
    val info = getLotteryStageInfo()
    info.result.isDefined
  }

  def isAllowedToStart: Boolean =
    trialStageService.isIterationFinished && !trialStageService.isNextIterationAvailable

  override def getLotteryStageInfo(): LotteryStageInfo = {
    val info = loadLotteryStageInfo().map(read[LotteryStageInfo])
    info match {
      case Some(definedInfo) ⇒ definedInfo
      case None ⇒
        requireState(isAllowedToStart)
        val lastIters = getLastIterations()
        val confidences = for (iter ← lastIters; confidence ← iter.confidence) yield confidence.toDouble
        val winChance = (confidences.sum / confidences.length.toDouble).toInt
        val definedInfo = LotteryStageInfo(
          winChance,
          lastItersCount = lastIterationsCount,
          lotterySelected = false,
          result = None
        )
        saveLotteryInfo(definedInfo)
        definedInfo
    }
  }

  def loadLotteryStageInfo(): Option[String] = {
    StageDataDAO.getStageData(userCode, Lottery.stageID, Lottery.stageInfoID).map(_.data)
  }

}
