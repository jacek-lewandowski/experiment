package net.enigma.model

/**
 * @author Jacek Lewandowski
 */
case class JustificationsStageInfo(
  variables: List[Variable],
  justified: Boolean,
  timestamp: Long = System.currentTimeMillis()
)
