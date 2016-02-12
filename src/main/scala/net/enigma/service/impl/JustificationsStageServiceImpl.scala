package net.enigma.service.impl

import org.json4s.native.Serialization._
import org.scalactic.Requirements._

import net.enigma.App
import net.enigma.db.StageDataDAO
import net.enigma.db.StageDataDAO.Justifications
import net.enigma.model._
import net.enigma.service.JustificationsStageService

/**
 * @author Jacek Lewandowski
 */
class JustificationsStageServiceImpl(userCode: String) extends JustificationsStageService {

  lazy val trialStageService = App.service.getTrialStageService

  import net.enigma.db.StageDataDAO.Justifications.formats

  override def setJustifiedVariables(variables: List[Variable]): Unit = {
    val info = getJustificationsStageInfo()
    requireState(!info.justified)
    val justificationsMap = variables.map(x ⇒ (x.id, x.justification)).toMap
    val updatedVariables = info.variables.map(v ⇒ justificationsMap.get(v.id) match {
      case Some(justification) ⇒ v.copy(justification = justification)
      case None ⇒ v
    })
    saveJustificationsInfo(info.copy(variables = updatedVariables, justified = true))
  }

  override def getVariablesToJustify(): List[Variable] = {
    val info = getJustificationsStageInfo()
    requireState(!info.justified)
    info.variables
  }

  def saveJustificationsInfo(json: String): Unit = {
    StageDataDAO.saveStageData(StageData(userCode, Justifications.stageID, Justifications.stageInfoID, 0, json))
  }

  def saveJustificationsInfo(info: JustificationsStageInfo): Unit = {
    val json = write(info)
    saveJustificationsInfo(json)
  }

  override def isStageCompleted: Boolean = {
    val info = getJustificationsStageInfo()
    info.justified
  }

  def isAllowedToStart: Boolean =
    trialStageService.isIterationFinished && !trialStageService.isNextIterationAvailable

  def getJustificationsStageInfo(): JustificationsStageInfo = {
    val info = loadJustificationsStageInfo().map(read[JustificationsStageInfo])
    info match {
      case Some(definedInfo) ⇒ definedInfo
      case None ⇒
        requireState(isAllowedToStart)
        val variables = trialStageService.getIterations(0, trialStageService.getStageInfo.sequences.length)
            .flatMap(_.essentialVars).distinct
        val definedInfo = JustificationsStageInfo(
          variables = variables,
          justified = false
        )
        saveJustificationsInfo(definedInfo)
        definedInfo
    }
  }

  def loadJustificationsStageInfo(): Option[String] = {
    StageDataDAO.getStageData(userCode, Justifications.stageID, Justifications.stageInfoID).map(_.data)
  }

}
