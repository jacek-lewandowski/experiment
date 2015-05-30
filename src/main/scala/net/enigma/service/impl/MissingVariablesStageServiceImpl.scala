package net.enigma.service.impl

import org.json4s.native.Serialization._
import org.scalactic.Requirements._

import net.enigma.db.StageDataDAO
import net.enigma.db.StageDataDAO.MissingVariables
import net.enigma.model._
import net.enigma.service.MissingVariablesStageService

/**
 * @author Jacek Lewandowski
 */
class MissingVariablesStageServiceImpl(userCode: String) extends MissingVariablesStageService {

  import net.enigma.db.StageDataDAO.MissingVariables.formats

  override def setMissingVariables(missingVariables: String): Unit = {
    val info = getMissingVariablesStageInfo()
    requireState(info.missingVariables.isEmpty)
    saveMissingVariablesInfo(info.copy(missingVariables = Some(missingVariables)))
  }

  def saveMissingVariablesInfo(json: String): Unit = {
    StageDataDAO.saveStageData(StageData(userCode, MissingVariables.stageID, MissingVariables.stageInfoID, 0, json))
  }

  def saveMissingVariablesInfo(info: MissingVariablesStageInfo): Unit = {
    val json = write(info)
    saveMissingVariablesInfo(json)
  }

  override def isStageCompleted: Boolean = {
    val info = getMissingVariablesStageInfo()
    info.missingVariables.isDefined
  }

  def isAllowedToStart: Boolean = true

  def getMissingVariablesStageInfo(): MissingVariablesStageInfo = {
    val info = loadMissingVariablesStageInfo().map(read[MissingVariablesStageInfo])
    info match {
      case Some(definedInfo) ⇒ definedInfo
      case None ⇒
        requireState(isAllowedToStart)
        val definedInfo = MissingVariablesStageInfo(None)
        saveMissingVariablesInfo(definedInfo)
        definedInfo
    }
  }

  def loadMissingVariablesStageInfo(): Option[String] = {
    StageDataDAO.getStageData(userCode, MissingVariables.stageID, MissingVariables.stageInfoID).map(_.data)
  }

}
