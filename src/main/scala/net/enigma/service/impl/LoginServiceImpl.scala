package net.enigma.service.impl

import net.enigma.App
import net.enigma.db.UserDAO
import net.enigma.model.User
import net.enigma.service.LoginService

/**
 * @author Jacek Lewandowski
 */
trait LoginServiceImpl extends LoginService {
  override def authenticate(code: String): Unit = {
    if (code.length < 4) {
      App.currentUser = None
    } else {
      App.service.getUser(code) match {
        case Some(user) ⇒
          App.currentUser = Some(code)
        case None if App.testMode ⇒
          val user = User(code = code, category = "test")
          UserDAO.addUser(code = code, category = "test")
          App.currentUser = Some(code)
        case _ ⇒
          App.currentUser = None
      }
    }
  }

  override def logout(): Unit = {
    App.currentUser = None
  }
}
