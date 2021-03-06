package net.enigma.service.impl

import scala.util.Random

import org.json4s.native.Serialization.{read, write}
import org.scalactic.Requirements._
import org.slf4j.LoggerFactory

import net.enigma.db.StageDataDAO.Trial
import net.enigma.db.{StageDataDAO, VariablesDAO}
import net.enigma.model.TrialAnswer.TrialAnswerType
import net.enigma.model.TrialStageInfo.IterationState
import net.enigma.model._
import net.enigma.service.TrialStageService

/**
 * @author Jacek Lewandowski
 */
class TrialStageServiceImpl(val userCode: String, _trialSetup: TrialSetup) extends TrialStageService {

  private val logger = LoggerFactory.getLogger(classOf[TrialStageServiceImpl])

  import StageDataDAO.Trial.formats

  override def getStageInfo: TrialStageInfo = {
    loadStageInfo() match {
      case Some(serialized) ⇒
        read[TrialStageInfo](serialized)
      case None ⇒
        val stageInfo = newStageInfo()
        updateStageInfo(stageInfo)
        stageInfo
    }
  }

  override def trialSetup: TrialSetup = getStageInfo.trialSetup

  def updateStageInfo(stageInfo: TrialStageInfo): Unit = {
    val serialized = write(stageInfo)
    saveStageInfo(serialized)
  }

  def saveFinishedIteration(iteration: Iteration): Unit = {
    val serialized = write(iteration)
    saveFinishedIteration(iteration.idx, serialized)
  }

  def saveFinishedIteration(iterationNumber: Int, json: String): Unit = {
    StageDataDAO.saveStageData(StageData(userCode, Trial.stageID, Trial.iterationID, iterationNumber, json))
  }

  def saveStageInfo(json: String): Unit = {
    StageDataDAO.saveStageData(StageData(userCode, Trial.stageID, Trial.stageInfoID, 0, json))
  }

  def loadStageInfo(): Option[String] = {
    StageDataDAO.getStageData(userCode, Trial.stageID, Trial.stageInfoID).map(_.data)
  }

  def loadIteration(idx: Int): Option[String] = {
    StageDataDAO.getStageData(userCode, Trial.stageID, Trial.iterationID, idx).map(_.data)
  }

  def getInitialVariables(): List[VariableDefinition] = {
    Random.shuffle(VariablesDAO.getVariablesDataSet("real"))
  }

  override def getIterations(from: Int, count: Int): List[Iteration] = {
    val itersCount = getStageInfo.sequences.size
    val itersIndices = if (from < 0) {
      (0 until itersCount).slice(itersCount + from, itersCount + from + count)
    } else {
      (0 until itersCount).slice(from, from + count)
    }

    (for (idx ← itersIndices; json ← loadIteration(idx)) yield read[Iteration](json)).toList
  }

  override def isIterationStarted: Boolean = {
    val info = getStageInfo
    info.iterationState == IterationState.started
  }

  override def isIterationFinished: Boolean = {
    val info = getStageInfo
    info.iterationState == IterationState.finished
  }

  override def isAwaitingAnswer: Boolean = isAwaitingAnswer(getStageInfo)

  override def isAwaitingConfidence: Boolean = isAwaitingConfidence(getStageInfo)

  override def isAwaitingEssentialVariables: Boolean = isAwaitingEssentialVariables(getStageInfo)

  override def isAwaitingVariableSelection: Boolean = isAwaitingVariableSelection(getStageInfo)

  override def isAwaitingNewVariables: Boolean = isAwaitingNewVariables(getStageInfo)

  override def availableScore: Int = {
    val stageInfo = getStageInfo
    stageInfo.trialSetup.totalScore - (stageInfo.curIter.get.selectedVars.size * stageInfo.trialSetup.unitPrice)
  }

  def isAwaitingAnswer(info: TrialStageInfo): Boolean = {
    info.iterationState == IterationState.started &&
        info.curIter.get.selectedAnswer.isEmpty &&
        info.trialSetup.selectedVariablesCountRange.contains(info.curIter.get.selectedVars.length)
  }

  override def setAnswer(answer: TrialAnswerType): Unit = {
    val stageInfo = getStageInfo
    requireState(isAwaitingAnswer(stageInfo))
    updateStageInfo(stageInfo.withCurIter(_.copy(selectedAnswer = Some(answer))))
  }

  def isAwaitingConfidence(info: TrialStageInfo): Boolean = {
    info.iterationState == IterationState.started &&
        info.curIter.get.selectedAnswer.isDefined &&
        info.curIter.get.confidence.isEmpty
  }

  override def setConfidence(confidence: Int): Unit = {
    val stageInfo = getStageInfo
    requireState(isAwaitingConfidence(stageInfo))
    updateStageInfo(stageInfo.withCurIter(_.copy(confidence = Some(confidence))))
  }

  def isAwaitingEssentialVariables(info: TrialStageInfo): Boolean = {
    info.iterationState == IterationState.started &&
        info.curIter.get.confidence.isDefined
  }

  override def setEssentialVariables(variables: List[Variable]): Unit = {
    val stageInfo = getStageInfo
    requireState(isAwaitingEssentialVariables(stageInfo))
    require(variables.map(_.id).distinct.size == stageInfo.trialSetup.essentialVarsCount)
    require(variables.forall(stageInfo.curIter.get.selectedVars.map(_.variable).contains))
    val updatedStageInfo = stageInfo.withCurIter(_.copy(essentialVars = variables)).copy(iterationState = IterationState.finished)
    updateStageInfo(updatedStageInfo)
    saveFinishedIteration(updatedStageInfo.curIter.get)
  }

  override def isAnswerProvided: Boolean =
    getStageInfo.curIter.fold(false)(_.selectedAnswer.isDefined)

  override def isConfidenceProvided: Boolean =
    getStageInfo.curIter.fold(false)(_.confidence.isDefined)

  override def isEssentialVariablesProvided: Boolean =
    getStageInfo.curIter.fold(false)(_.essentialVars.nonEmpty)

  private def randomValue: TrialAnswerType = Random.shuffle(Seq(TrialAnswer.Plus, TrialAnswer.Minus)).head

  def isAwaitingVariableSelection(info: TrialStageInfo): Boolean = {
    info.iterationState == IterationState.started &&
        info.curIter.get.selectedVars.length < info.trialSetup.maxSelectedVariablesCount &&
        info.curIter.get.selectedAnswer.isEmpty
  }

  override def selectVariable(selectedVar: Variable): VariableValue = {
    val info = getStageInfo
    requireState(isAwaitingVariableSelection(info))
    val curIter = info.curIter.get
    require(!curIter.selectedVars.map(_.variable.id).contains(selectedVar.id))
    require(curIter.initVars.exists(v ⇒ v.id == selectedVar.id && v.title == selectedVar.title))
    val curSeq = curIter.sequence
    val varData = curIter.initVars.find(_.id == selectedVar.id).get
    val seqPos = curIter.selectedVars.length

    val value = curSeq.lift(seqPos).getOrElse(randomValue)

    val variableValue = value match {
      case TrialAnswer.Plus ⇒
        VariableValue(selectedVar, value, varData.plus)
      case TrialAnswer.Minus ⇒
        VariableValue(selectedVar, value, varData.minus)
    }

    updateStageInfo(info.withCurIter(_.copy(selectedVars = curIter.selectedVars :+ variableValue)))
    variableValue
  }

  override def isNextIterationAvailable: Boolean = {
    val info = getStageInfo
    info.curIter.map(_.idx + 1).getOrElse(0) < info.sequences.length
  }

  def isAwaitingNewVariables(info: TrialStageInfo): Boolean = {
    (info.iterationState == IterationState.notReady || info.iterationState == IterationState.finished) &&
        info.curIter.map(_.idx + 1).getOrElse(0) < info.sequences.length
  }

  override def prepareVariables(): List[Variable] = {
    val info = getStageInfo
    requireState(isAwaitingNewVariables(info))
    newIteration(info).initVars.map(vd ⇒ Variable(vd.id, vd.title))
  }

  private def newIteration(info: TrialStageInfo): Iteration = {
    info.curIter match {
      case Some(iter) ⇒ saveFinishedIteration(iter)
      case None ⇒
    }

    val iterationIdx = info.curIter.map(_.idx + 1).getOrElse(0)
    val sequence = info.sequences(iterationIdx)

    val initVars = getInitialVariables()

    val iteration = new Iteration(
      idx = iterationIdx,
      sequence = sequence,
      initVars = initVars
    )

    updateStageInfo(info.copy(curIter = Some(iteration), iterationState = IterationState.started))
    iteration
  }

  private def newStageInfo(): TrialStageInfo = {
    val sequences = new SequencesGeneratorImpl(_trialSetup).generateSequences()
    TrialStageInfo(_trialSetup, sequences)
  }

  override def getSelectedVariables(): List[VariableValue] = {
    val stageInfo = getStageInfo
    requireState(stageInfo.curIter.isDefined)
    stageInfo.curIter.get.selectedVars
  }

  override def getPreparedVariables(): List[VariableDefinition] = {
    val stageInfo = getStageInfo
    requireState(stageInfo.iterationState != IterationState.notReady)
    stageInfo.curIter.get.initVars
  }
}

object TrialStageServiceImpl {


}
