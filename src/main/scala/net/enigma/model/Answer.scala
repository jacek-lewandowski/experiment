package net.enigma.model

/**
 * @author Jacek Lewandowski
 */
case class Answer(
  questionId: Int,
  caption: String,
  required: Boolean,
  validatorName: String,
  validatorParams: Map[String, String],
  answer: String,
  userCode: String
)
