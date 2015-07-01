package net.enigma.service.impl

import scala.util.Random

import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory

import net.enigma.App
import net.enigma.db.{GroupDAO, UserDAO}
import net.enigma.model.{Group, User}
import net.enigma.service.UserService

/**
 * @author Jacek Lewandowski
 */
trait UserServiceImpl extends UserService {

  private val logger = LoggerFactory.getLogger(classOf[UserServiceImpl])

  override def checkForCompletedStages(requiredKeys: Set[String], forbiddenKeys: Set[String]): Boolean = {
    val currentUserCode = App.currentUser.get
    val completedStages = UserDAO.getUserCompletedStages(currentUserCode)

    logger.info(s"Checking for required $requiredKeys and forbidden $forbiddenKeys while the user completed $completedStages")

    val containsRequired = requiredKeys.forall(completedStages.contains)
    val missForbidden = !forbiddenKeys.exists(completedStages.contains)
    containsRequired && missForbidden
  }

  override def completeStage(stageId: String): Unit = {
    val currentUserCode = App.currentUser.get
    UserDAO.addToUserCompletedStages(currentUserCode, stageId)
  }

  override def setCurrentStage(stageId: String): Unit = {
    val currentUserCode = App.currentUser.get
    UserDAO.setCurrentStage(currentUserCode, stageId)
    logger.info(s"Set current stage to $stageId")
  }

  override def getCurrentStage(): Option[String] = {
    val r = for (currentUserCode <- App.currentUser) yield {
      val result = UserDAO.getCurrentStage(currentUserCode)
      logger.info(s"Got current stage $result")
      result
    }
    r.getOrElse(None)
  }

  override def setEmailAddress(emailAddress: String): Unit = {
    val currentUserCode = App.currentUser.get
    UserDAO.setEmailAddress(currentUserCode, emailAddress)
  }

  override def getUser(code: String): Option[User] = {
    UserDAO.getUser(code)
  }

  override def getAllUsers(): Seq[User] = {
    UserDAO.getAllUsers()
  }

  def newCode(length: Int): String = {
    val data = new Array[Byte](length)
    App.random.nextBytes(data)
    val code = BigInt(data).abs.toString(36)
    code.take(length)
  }

  override def generateNewUser(groupName: String): String = {
    val code = newCode(25)
    UserDAO.addUser(code, groupName)
    code
  }

  override def generateNewGroup(groupName: String): String = {
    val code = newCode(25)
    GroupDAO.addGroup(code, groupName)
    code
  }

  override def getGroup(code: String): Option[Group] = {
    GroupDAO.getGroup(code)
  }
}
