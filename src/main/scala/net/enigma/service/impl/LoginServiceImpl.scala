package net.enigma.service.impl

import net.enigma.App
import net.enigma.db.UserDAO
import net.enigma.model.User
import net.enigma.model.User.UserState
import net.enigma.service.LoginService

/**
 * @author Jacek Lewandowski
 */
trait LoginServiceImpl extends LoginService {
  override def authenticate(code: String): Unit = {
    if (code.length > 3) {
      val user = User(code, UserState.NOT_OPENED.name)
      UserDAO.addUser(code)
      App.currentUser = Some(user)
    } else {
      User(code, UserState.UNKNOWN.name)
      App.currentUser = None
    }
  }

  override def logout(): Unit = {
    App.currentUser = None
  }
}
