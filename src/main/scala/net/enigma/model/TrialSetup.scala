package net.enigma.model

import org.scalactic.Requirements._

/**
 * @author Jacek Lewandowski
 */
case class TrialSetup(
  minSelectedVariablesCount: Int,
  maxSelectedVariablesCount: Int,
  totalScore: Int,
  unitPrice: Int,
  essentialVarsCount: Int,
  sequenceSetup: String,
  sequenceLength: Int
) {
  require(essentialVarsCount < minSelectedVariablesCount)
  require(((maxSelectedVariablesCount - minSelectedVariablesCount) * unitPrice) <= totalScore)

  def selectedVariablesCountRange = minSelectedVariablesCount to maxSelectedVariablesCount
}
