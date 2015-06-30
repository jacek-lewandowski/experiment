package net.enigma.service

import net.enigma.model.User

/**
 * @author Jacek Lewandowski
 */
trait UserService {
  def setEmailAddress(emailAddress: String): Unit

  def checkForCompletedStages(requiredStages: Set[String], forbiddenStages: Set[String]): Boolean

  def completeStage(id: String)

  def setCurrentStage(stageId: String): Unit

  def getCurrentStage(): Option[String]

  def getUser(code: String): Option[User]

  def getAllUsers(): Seq[User]

  def generateNewUser(groupName: String): String

  def generateNewGroup(groupName: String): String
}
