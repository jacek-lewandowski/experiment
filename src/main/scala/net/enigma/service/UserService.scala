package net.enigma.service

/**
 * @author Jacek Lewandowski
 */
trait UserService {
  def setEmailAddress(emailAddress: String): Unit

  def checkForCompletedStages(requiredStages: Set[String], forbiddenStages: Set[String]): Boolean

  def completeStage(id: String)

  def setCurrentStage(stageId: String): Unit

  def getCurrentStage(): Option[String]

}
