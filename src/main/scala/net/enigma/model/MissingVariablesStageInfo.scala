package net.enigma.model

/**
 * @author Jacek Lewandowski
 */
case class MissingVariablesStageInfo(
  missingVariables: Option[String],
  timestamp: Long = System.currentTimeMillis()
)
