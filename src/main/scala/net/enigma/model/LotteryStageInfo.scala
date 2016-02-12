package net.enigma.model

/**
 * @author Jacek Lewandowski
 */
case class LotteryStageInfo(
  winChance: Int,
  lastItersCount: Int,
  selectedIterIdx: Int,
  lotterySelected: Boolean,
  result: Option[Boolean],
  timestamp: Long = System.currentTimeMillis()
)
