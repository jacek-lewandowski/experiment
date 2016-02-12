package net.enigma.service

/**
 * @author Jacek Lewandowski
 */
trait LoginService {
  def authenticate(code: String): Unit

  def logout(): Unit
}
