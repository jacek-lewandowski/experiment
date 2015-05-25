package net.enigma.model

/**
 * @author Jacek Lewandowski
 */
case class LotteryStageInfo(
  winChance: Int,
  lastItersCount: Int,
  lotterySelected: Boolean,
  result: Option[Boolean]
)
