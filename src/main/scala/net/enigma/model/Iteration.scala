package net.enigma.model

import net.enigma.model.TrialAnswer._

/**
 * @param idx the number of iteration starting from 0
 * @param initVars the set of variables presented to the user, in order
 * @param selectedVars variables selected by the user, in order
 * @param sequence a sequence of values presented to the user
 * @param selectedAnswer an answer selected by the user
 * @param confidence a confidence level entered by the user
 * @param explanation an explanation provided by the user
 * @param essentialVars most important variables selected by the user among those which are in `variables` collection
 *
 * @author Jacek Lewandowski

 */
case class Iteration(
  idx: Int,
  sequence: List[TrialAnswerType],
  initVars: List[VariableDefinition] = Nil,
  selectedVars: List[VariableValue] = Nil,
  selectedAnswer: Option[TrialAnswerType] = None,
  confidence: Option[Int] = None,
  explanation: Option[String] = None,
  essentialVars: List[Variable] = Nil
) {
  lazy val isClear: Boolean =
    sequence.forall(_ == sequence.head)

  lazy val isAnswerCorrect: Option[Boolean] =
    if (isClear) Some(selectedAnswer.get == sequence.head) else None
}
