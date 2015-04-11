package net.enigma.model

import net.enigma.model.TrialAnswer.TrialAnswerType

/**
 * @author Jacek Lewandowski
 */
case class VariableValue(variable: Variable, value: TrialAnswerType, description: String)
