package net.enigma.model

/**
 * @author Jacek Lewandowski
 */
case class Question(
  questionsSet: String,
  id: Int,
  caption: String,
  required: Boolean,
  validatorName: String,
  validatorParams: Map[String, String]
)
