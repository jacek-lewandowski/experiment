package net.enigma.service.impl

import scala.util.Random

import org.json4s.native.Serialization._
import org.scalactic.Requirements._

import net.enigma.db.StageDataDAO.Variables
import net.enigma.db.{StageDataDAO, UserDAO, VariablesDAO}
import net.enigma.model.VariablesStageInfo.VariablesState
import net.enigma.model._
import net.enigma.service.VariablesStageService

/**
 * @author Jacek Lewandowski
 */
class VariablesStageServiceImpl(val userCode: String, _variablesSetup: VariablesSetup) extends VariablesStageService {

  def dataSet = "real"

  import StageDataDAO.Variables.formats

  override def variablesSetup: VariablesSetup = {
    val info = getVariablesStageInfo()
    info.variablesSetup
  }

  def loadVariablesStageInfo(): Option[String] = {
    StageDataDAO.getStageData(userCode, Variables.stageID, Variables.stageInfoID).map(_.data)
  }

  override def getVariablesStageInfo(): VariablesStageInfo = {
    val info = loadVariablesStageInfo().map(json ⇒ read[VariablesStageInfo](json))
    info match {
      case Some(definedInfo) ⇒ definedInfo
      case None ⇒
        val definedInfo = VariablesStageInfo(Nil, VariablesState.notReady, _variablesSetup)
        saveVariablesStageInfo(definedInfo)
        definedInfo
    }
  }

  def saveVariablesStageInfo(json: String): Unit = {
    StageDataDAO.saveStageData(StageData(userCode, Variables.stageID, Variables.stageInfoID, 0, json))
  }

  def saveVariablesStageInfo(info: VariablesStageInfo): Unit = {
    val json = write(info)
    saveVariablesStageInfo(json)
  }

  override def prepareVariables(): Unit = {
    val info = getVariablesStageInfo()
    requireState(info.state == VariablesState.notReady)
    val variables = selectRandomVariables()
    saveVariablesStageInfo(info.copy(variables = variables, state = VariablesState.prepared))
  }

  override def getVariablesForSelection(): List[Variable] = {
    val info = getVariablesStageInfo()
    requireState(info.state == VariablesState.prepared)
    info.variables
  }

  override def getVariablesForReordering(): List[Variable] = {
    val info = getVariablesStageInfo()
    requireState(info.state == VariablesState.selected)
    info.variables
  }

  override def getVariablesForScoring(): List[Variable] = {
    val info = getVariablesStageInfo()
    requireState(info.state == VariablesState.ordered)
    info.variables
  }

  override def getVariablesForExperiment(): List[Variable] = {
    val info = getVariablesStageInfo()
    requireState(info.state == VariablesState.scored)
    info.variables
  }

  override def setSelectedVariables(variables: List[Variable]): Unit = {
    val info = getVariablesStageInfo()
    requireState(info.state == VariablesState.prepared)
    require(variables.forall(v ⇒ v.score.isEmpty && v.ordinalNumber.isEmpty))
    require(variables.forall(v ⇒ info.variables.contains(v)))
    saveVariablesStageInfo(info.copy(variables = variables, state = VariablesState.selected))
  }

  override def setReorderedVariables(variables: List[Variable]): Unit = {
    val info = getVariablesStageInfo()
    requireState(info.state == VariablesState.selected)
    require(variables.forall(v ⇒ v.score.isEmpty && v.ordinalNumber.nonEmpty))
    require(variables.forall(v ⇒ info.variables.contains(v.copy(ordinalNumber = None))))
    saveVariablesStageInfo(info.copy(variables = variables, state = VariablesState.ordered))
  }

  override def setScoredVariables(variables: List[Variable]): Unit = {
    val info = getVariablesStageInfo()
    requireState(info.state == VariablesState.ordered)
    require(variables.forall(v ⇒ v.score.nonEmpty && v.ordinalNumber.nonEmpty))
    require(variables.flatMap(_.score).sum == 100)
    require(variables.forall(v ⇒ info.variables.contains(v.copy(score = None))))
    saveVariablesStageInfo(info.copy(variables = variables, state = VariablesState.scored))
  }

  protected def selectRandomVariables(): List[Variable] = {
    val variablesData = VariablesDAO.getVariablesDataSet(dataSet)
    Random.shuffle(
      variablesData.map(variableData ⇒
        Variable(
          variableData.id,
          variableData.title
        )
      )
    )
  }

  protected def getCompletedStages: Set[String] = {
    UserDAO.getUserCompletedStages(userCode)
  }

  protected def completeStage(stageId: String): Unit = {
    UserDAO.addToUserCompletedStages(userCode, stageId)
  }

}
