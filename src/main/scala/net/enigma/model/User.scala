package net.enigma.model

import scala.language.implicitConversions

import net.enigma.model.User.UserState
import net.enigma.model.User.UserState.{AbleToProceed, UNKNOWN}

/**
 * @author Jacek Lewandowski
 */
case class User(
  code: String,
  stateString: String = UNKNOWN.productPrefix,
  stagesStatesStrings: Map[String, String] = Map.empty
) {

  lazy val state: UserState = UserState(stateString).get

  lazy val stagesStates = stagesStatesStrings.flatMap { case (key, value) ⇒ UserState(value).map((key, _)) }

  def isAbleToProceed: Boolean = this.state match {
    case _: AbleToProceed ⇒ true
    case _ ⇒ false
  }

  def copy(code: String = code, state: UserState = state, stagesStates: Map[String, UserState]): User =
    User(code, state.name, stagesStatesStrings = stagesStates.mapValues(_.name))


}

object User {

  sealed trait UserState extends Serializable {
    self: Product ⇒

    def name = this.productPrefix
  }

  object UserState {

    sealed trait AbleToProceed extends UserState {
      self: Product ⇒
    }

    /** The user has never logged in */
    case object NOT_OPENED extends AbleToProceed

    /** The user has logged in before, but he can continue / restart */
    case object CONTINUABLE extends AbleToProceed

    /** The user cannot continue, but the experiment has not been finished */
    case object NOT_CONTINUABLE extends UserState

    /** The user has finished the experiment */
    case object FINISHED extends UserState

    /** User not found */
    case object UNKNOWN extends UserState

    lazy val values: Set[UserState] = Set(NOT_OPENED, CONTINUABLE, NOT_CONTINUABLE, FINISHED, UNKNOWN)

    private lazy val valuesMap: Map[String, UserState] = values.map(value ⇒ (value.name, value)).toMap

    def apply(name: String): Option[UserState] = valuesMap.get(name)

  }

}