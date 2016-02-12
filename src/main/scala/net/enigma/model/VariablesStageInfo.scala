package net.enigma.model

import net.enigma.model.VariablesStageInfo.{VariablesState, VariablesStateType}

/**
 * @author Jacek Lewandowski
 */
case class VariablesStageInfo(
  variables: List[Variable],
  state: VariablesStateType = VariablesState.notReady,
  variablesSetup: VariablesSetup,
  timestamp: Long = System.currentTimeMillis()
)

object VariablesStageInfo {
  type VariablesStateType = VariablesState.Value

  object VariablesState extends Enumeration {
    val notReady = Value(0, "not-ready")
    val prepared = Value(1, "prepared")
    val selected = Value(2, "selected")
    val ordered = Value(3, "ordered")
    val scored = Value(4, "scored")
  }

}
