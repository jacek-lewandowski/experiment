package net.enigma.service

import net.enigma.model.Variable

/**
 * @author Jacek Lewandowski
 */
trait JustificationsStageService {
  def setJustifiedVariables(variables: List[Variable]): Unit

  def getVariablesToJustify(): List[Variable]

  def isStageCompleted: Boolean
}
