package net.enigma.views

import net.enigma.Utils._
import net.enigma.views.components._

/**
 * @author Jacek Lewandowski
 */
trait VariablesScoringView extends AbstractView {
  val scorer = new VariablesScorer().withSizeFull

  addInfo(instructions)

  content.addComponents(scorer)
  content.setExpandRatio(scorer, 1)

  def instructions: String
}
