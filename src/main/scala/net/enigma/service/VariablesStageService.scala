package net.enigma.service

import net.enigma.model._

/**
 * @author Jacek Lewandowski
 */
trait VariablesStageService {
  def prepareVariables(): Unit

  def getVariablesForSelection(): List[Variable]

  def setSelectedVariables(variables: List[Variable]): Unit

  def getVariablesForReordering(): List[Variable]

  def setReorderedVariables(variables: List[Variable]): Unit

  def getVariablesForScoring(): List[Variable]

  def setScoredVariables(variables: List[Variable]): Unit

  def getVariablesForExperiment(): List[Variable]

  def getVariablesSetup(): VariablesSetup
}
