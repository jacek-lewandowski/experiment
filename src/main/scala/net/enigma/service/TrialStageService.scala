package net.enigma.service

import net.enigma.model.TrialAnswer.TrialAnswerType
import net.enigma.model._

/**
 * @author Jacek Lewandowski
 */
trait TrialStageService {
  def getPreparedVariables(): List[VariableDefinition]

  def getSelectedVariables(): List[Variable]

  def isMostImportantVariablesProvided: Boolean

  def isConfidenceProvided: Boolean

  def isAnswerProvided: Boolean

  def trialSetup: TrialSetup

  def isIterationStarted: Boolean

  def isIterationFinished: Boolean

  def isNextIterationAvailable: Boolean

  def prepareVariables(): List[Variable]

  def selectVariable(selectedVariable: Variable): VariableValue

  def setAnswer(answer: TrialAnswerType)

  def setConfidence(confidence: Int)

  def setEssentialVariables(variables: List[Variable])

  def getIterations(from: Int, count: Int): List[Iteration]

  def getStageInfo: TrialStageInfo
}
