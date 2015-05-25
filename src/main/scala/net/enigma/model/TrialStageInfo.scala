package net.enigma.model

import net.enigma.model.TrialAnswer._
import net.enigma.model.TrialStageInfo._

/**
 * @author Jacek Lewandowski
 */
case class TrialStageInfo(
  trialSetup: TrialSetup,
  sequences: List[List[TrialAnswerType]],
  curIter: Option[Iteration] = None,
  iterationState: IterationStateType = IterationState.notReady
) {
  def withCurIter(f: Iteration ⇒ Iteration): TrialStageInfo = {
    copy(curIter = Some(f(curIter.get)))
  }
}

object TrialStageInfo {
  type IterationStateType = IterationState.Value

  object IterationState extends Enumeration {
    val notReady = Value(0, "not-ready")
    val started = Value(1, "started")
    val finished = Value(2, "finished")
  }

}
