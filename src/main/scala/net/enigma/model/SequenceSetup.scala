package net.enigma.model

import net.enigma.model.TrialAnswer.TrialAnswerType

/**
 * @author Jacek Lewandowski
 */
case class SequenceSetup(
  sequences: List[List[TrialAnswerType]],
  shuffleRange: Option[ShuffleRange]
)
