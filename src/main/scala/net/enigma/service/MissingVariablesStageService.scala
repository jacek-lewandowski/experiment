package net.enigma.service

/**
 * @author Jacek Lewandowski
 */
trait MissingVariablesStageService {
  def setMissingVariables(missingVariables: String): Unit

  def isStageCompleted: Boolean
}
