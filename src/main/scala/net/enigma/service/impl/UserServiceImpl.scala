package net.enigma.service.impl

import org.slf4j.LoggerFactory

import net.enigma.App
import net.enigma.db.UserDAO
import net.enigma.model.User
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
    val currentUserCode = App.currentUser.get
    val result = UserDAO.getCurrentStage(currentUserCode)
    logger.info(s"Got current stage $result")
    result
  }

  override def setEmailAddress(emailAddress: String): Unit = {
    val currentUserCode = App.currentUser.get
    UserDAO.setEmailAddress(currentUserCode, emailAddress)
  }

  override def getUser(code: String): Option[User] = {
    UserDAO.getUser(code)
  }
}
