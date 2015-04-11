package net.enigma.service.impl

import scala.util.Random

import net.enigma.TextResources
import net.enigma.db.{UserDAO, UserVariablesDAO, VariablesDAO}
import net.enigma.model._
import net.enigma.service.VariablesStageService

/**
 * @author Jacek Lewandowski
 */
class VariablesStageServiceImpl(val userCode: String) extends VariablesStageService {

  def dataSet = "real"

  object UserStages {
    val variablesPrepared = "experiment.variables.prepared"
    val variablesSelected = "experiment.variables.selected"
    val variablesReordered = "experiment.variables.reordered"
    val variablesScored = "experiment.variables.scored"
  }

  override def prepareVariables(): Unit = {
    saveVariables(UserStages.variablesPrepared, None, selectRandomVariables)
  }

  override def getVariablesForSelection(): List[Variable] = {
    getVariables(UserStages.variablesPrepared)
  }

  override def getVariablesForReordering(): List[Variable] = {
    getVariables(UserStages.variablesSelected)
  }

  override def getVariablesForScoring(): List[Variable] = {
    getVariables(UserStages.variablesReordered)
  }

  override def getVariablesForExperiment(): List[Variable] = {
    getVariables(UserStages.variablesScored)
  }

  override def setSelectedVariables(variables: List[Variable]): Unit = {
    saveVariables(UserStages.variablesSelected, Some(UserStages.variablesPrepared), variables)
  }

  override def setReorderedVariables(variables: List[Variable]): Unit = {
    saveVariables(UserStages.variablesReordered, Some(UserStages.variablesSelected), variables)
  }

  override def setScoredVariables(variables: List[Variable]): Unit = {
    saveVariables(UserStages.variablesScored, Some(UserStages.variablesReordered), variables)
  }

  override def getVariablesSetup(): VariablesSetup = {
    val maxSelectableVariables = TextResources.Setup.Variables.MaxSelectedVariables.toInt
    val minSelectableVariables = TextResources.Setup.Variables.MinSelectedVariables.toInt

    VariablesSetup(
      maxSelectableVariables,
      minSelectableVariables
    )
  }

  protected def getVariables(predecessor: String): List[Variable] = {
    val completedStages = getCompletedStages

    if (!completedStages.contains(predecessor)) {
      Nil
    } else {
      UserVariablesDAO.getUserVariables(dataSet, userCode, predecessor)
    }
  }

  protected def saveVariables(thisStage: String, predecessor: Option[String], variables: ⇒ List[Variable]): Unit = {
    val completedStages = getCompletedStages

    if (predecessor.fold(true)(completedStages.contains) && !completedStages.contains(thisStage)) {
      UserVariablesDAO.saveUserVariables(dataSet, userCode, thisStage, variables)
      completeStage(thisStage)
    }
  }

  protected def selectRandomVariables: List[Variable] = {
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
