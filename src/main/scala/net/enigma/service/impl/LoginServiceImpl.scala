package net.enigma.service.impl

import javax.servlet.http.Cookie

import com.vaadin.server.VaadinService
import org.slf4j.LoggerFactory

import net.enigma.App
import net.enigma.db.UserDAO
import net.enigma.model.{Group, User}
import net.enigma.service.LoginService

/**
 * @author Jacek Lewandowski
 */
trait LoginServiceImpl extends LoginService {
  private val logger = LoggerFactory.getLogger("net.enigma.service.impl.LoginService")

  override def authenticate(code: String): Unit = {
    println(s"Authenticating $code")
    if (code.length < 4) {
      println("Length < 4")
      App.currentUser = None
    } else {
      App.service.getUser(code) match {
        case Some(user) ⇒
          println("User found")
          App.currentUser = Some(code)
        case None =>
          maybeCreateUser(code)
      }
    }
  }

  def maybeCreateUser(code: String): Unit = {
    println("maybeCreateUser($code)")
    App.service.getGroup(code) match {
      case Some(group) =>
        println("Group exists")
        maybeCreateUser(group)
      case None if App.testMode =>
        println("Test mode")
        val user = User(code = code, category = "test")
        UserDAO.addUser(user.code, user.category)
        App.currentUser = Some(user.code)
      case _ ⇒
        println("None")
        App.currentUser = None
    }
  }

  def maybeCreateUser(group: Group): Unit = {
    if (getGroupCodeFromCookie().contains(group.code)) {
      println("setUserFromCookie")
      setUserFromCookie(group)
    } else {
      println("createNewUser")
      createNewUser(group)
    }
  }

  def setUserFromCookie(group: Group): Unit = {
    val existingUserCode =
      for (userCode <- getUserCodeFromCookie();
           user <- App.service.getUser(userCode)) yield user.code

    App.currentUser = existingUserCode
  }

  def createNewUser(group: Group): Unit = {
    val newUserCode = App.service.generateNewUser(group.category)
    val user = User(code = newUserCode, category = group.category)
    UserDAO.addUser(user.code, user.category)
    App.currentUser = Some(user.code)
    setUserCookie(user.code, group.code)
  }

  def setUserCookie(userCode: String, groupCode: String): Unit = {
    val cookies = Seq(
      new Cookie("user-code", userCode),
      new Cookie("group-code", groupCode)
    )

    cookies.foreach { cookie =>
      cookie.setMaxAge(3 * 24 * 3600)
      cookie.setPath(VaadinService.getCurrentRequest.getContextPath)
      VaadinService.getCurrentResponse.addCookie(cookie)
    }
  }

  def getUserCodeFromCookie(): Option[String] = {
    val cookies = VaadinService.getCurrentRequest.getCookies
    cookies.find(_.getName == "user-code").map(_.getValue)
  }

  def getGroupCodeFromCookie(): Option[String] = {
    val cookies = VaadinService.getCurrentRequest.getCookies
    cookies.find(_.getName == "group-code").map(_.getValue)
  }


  override def logout(): Unit = {
    App.currentUser = None
  }
}
