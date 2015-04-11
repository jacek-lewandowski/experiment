package net.enigma.model

/**
 * @author Jacek Lewandowski
 */
object TrialAnswer extends Enumeration{
  type TrialAnswerType = Value
  val Plus = Value(1, "Plus")
  val Minus = Value(0, "Minus")
}
