package net.enigma

import net.enigma.model.User

/**
 * @author Jacek Lewandowski
 */
class LogicController {
  def authenticate(code: String): Option[User] = {
    if (code != null && code.length % 2 == 0) Some(User(code)) else None
  }
}
