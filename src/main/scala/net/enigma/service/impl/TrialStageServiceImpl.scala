package net.enigma.service.impl

import scala.util.Random

import org.json4s.native.Serialization.{read, write}
import org.scalactic.Requirements._
import org.slf4j.LoggerFactory

import net.enigma.db.StageDataDAO.Trial
import net.enigma.db.{StageDataDAO, VariablesDAO}
import net.enigma.model.TrialAnswer.TrialAnswerType
import net.enigma.model._
import net.enigma.service.TrialStageService

/**
 * @author Jacek Lewandowski
 */
class TrialStageServiceImpl(val userCode: String, _trialSetup: TrialSetup) extends TrialStageService {

  import net.enigma.service.impl.TrialStageServiceImpl._

  private val logger = LoggerFactory.getLogger(classOf[TrialStageServiceImpl])

  implicit val format = StageDataDAO.formats

  override def getStageInfo: StageInfo = {
    loadStageInfo() match {
      case Some(serialized) ⇒
        read[StageInfo](serialized)
      case None ⇒
        val stageInfo = newStageInfo()
        updateStageInfo(stageInfo)
        stageInfo
    }
  }

  override def trialSetup: TrialSetup = getStageInfo.trialSetup

  def updateStageInfo(stageInfo: StageInfo): Unit = {
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
      (0 until itersCount).slice(itersCount - from, itersCount - from + count)
    } else {
      (1 to itersCount).slice(from, from + count)
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

  def isAwaitingAnswer(info: StageInfo): Boolean = {
    info.iterationState == IterationState.started &&
        info.curIter.get.selectedAnswer.isEmpty &&
        info.trialSetup.selectedVariablesCountRange.contains(info.curIter.get.selectedVars.length)
  }

  override def setAnswer(answer: TrialAnswerType): Unit = {
    val stageInfo = getStageInfo
    requireState(isAwaitingAnswer(stageInfo))
    updateStageInfo(stageInfo.withCurIter(_.copy(selectedAnswer = Some(answer))))
  }

  def isAwaitingConfidence(info: StageInfo): Boolean = {
    info.iterationState == IterationState.started &&
        info.curIter.get.selectedAnswer.isDefined &&
        info.curIter.get.confidence.isEmpty
  }

  override def setConfidence(confidence: Int): Unit = {
    val stageInfo = getStageInfo
    requireState(isAwaitingConfidence(stageInfo))
    updateStageInfo(stageInfo.withCurIter(_.copy(confidence = Some(confidence))))
  }

  def isAwaitingExplanation(info: StageInfo): Boolean = {
    info.iterationState == IterationState.started &&
        info.curIter.get.confidence.isDefined &&
        info.curIter.get.explanation.isEmpty
  }

  override def setExplanation(explanation: String): Unit = {
    val stageInfo = getStageInfo
    requireState(isAwaitingExplanation(stageInfo))
    updateStageInfo(stageInfo.withCurIter(_.copy(explanation = Some(explanation))))
  }

  def isAwaitingEssentialVariables(info: StageInfo): Boolean = {
    info.iterationState == IterationState.started &&
        info.curIter.get.explanation.isDefined
  }

  override def setEssentialVariables(variables: List[Variable]): Unit = {
    val stageInfo = getStageInfo
    requireState(isAwaitingEssentialVariables(stageInfo))
    require(variables.map(_.id).distinct.size == stageInfo.trialSetup.essentialVarsCount)
    require(variables.forall(stageInfo.curIter.get.selectedVars.map(_.variable).contains))
    updateStageInfo(stageInfo.withCurIter(_.copy(essentialVars = variables)).copy(iterationState = IterationState.finished))
  }

  override def isAnswerProvided: Boolean =
    getStageInfo.curIter.fold(false)(_.selectedAnswer.isDefined)

  override def isConfidenceProvided: Boolean =
    getStageInfo.curIter.fold(false)(_.confidence.isDefined)

  override def isExplanationProvided: Boolean =
    getStageInfo.curIter.fold(false)(_.explanation.isDefined)

  override def isMostImportantVariablesProvided: Boolean =
    getStageInfo.curIter.fold(false)(_.essentialVars.nonEmpty)

  private def randomValue: TrialAnswerType = Random.shuffle(Seq(TrialAnswer.Plus, TrialAnswer.Minus)).head

  def isAwaitingVariableSelection(info: StageInfo): Boolean = {
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

  def isAwaitingNewVariables(info: StageInfo): Boolean = {
    (info.iterationState == IterationState.notReady || info.iterationState == IterationState.finished) &&
        info.curIter.map(_.idx + 1).getOrElse(0) < info.sequences.length
  }

  override def prepareVariables(): List[Variable] = {
    val info = getStageInfo
    requireState(isAwaitingNewVariables(info))
    newIteration(info).initVars.map(vd ⇒ Variable(vd.id, vd.title))
  }

  private def newIteration(info: StageInfo): Iteration = {
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

  private def newStageInfo(): StageInfo = {
    val sequences = new SequencesGeneratorImpl(_trialSetup).generateSequences()
    StageInfo(_trialSetup, sequences)
  }

  override def getSelectedVariables(): List[Variable] = {
    val stageInfo = getStageInfo
    requireState(stageInfo.curIter.isDefined)
    stageInfo.curIter.get.selectedVars.map(_.variable)
  }

  override def getPreparedVariables(): List[VariableDefinition] = {
    val stageInfo = getStageInfo
    requireState(stageInfo.iterationState != IterationState.notReady)
    stageInfo.curIter.get.initVars
  }
}

object TrialStageServiceImpl {

  /**
   * @param idx the number of iteration starting from 0
   * @param initVars the set of variables presented to the user, in order
   * @param selectedVars variables selected by the user, in order
   * @param sequence a sequence of values presented to the user
   * @param selectedAnswer an answer selected by the user
   * @param confidence a confidence level entered by the user
   * @param explanation an explanation provided by the user
   * @param essentialVars most important variables selected by the user among those which are in `variables` collection
   */
  case class Iteration(
    idx: Int,
    sequence: List[TrialAnswerType],
    initVars: List[VariableDefinition] = Nil,
    selectedVars: List[VariableValue] = Nil,
    selectedAnswer: Option[TrialAnswerType] = None,
    confidence: Option[Int] = None,
    explanation: Option[String] = None,
    essentialVars: List[Variable] = Nil
  ) {
    lazy val isClear: Boolean =
      sequence.forall(_ == sequence.head)

    lazy val isAnswerCorrect: Option[Boolean] =
      if (isClear) Some(selectedAnswer.get == sequence.head) else None
  }

  case class StageInfo(
    trialSetup: TrialSetup,
    sequences: List[List[TrialAnswerType]],
    curIter: Option[Iteration] = None,
    iterationState: IterationStateType = IterationState.notReady
  ) {
    def withCurIter(f: Iteration ⇒ Iteration): StageInfo = {
      copy(curIter = Some(f(curIter.get)))
    }
  }

  type IterationStateType = IterationState.Value

  object IterationState extends Enumeration {
    val notReady = Value(0, "not-ready")
    val started = Value(1, "started")
    val finished = Value(2, "finished")
  }

}
