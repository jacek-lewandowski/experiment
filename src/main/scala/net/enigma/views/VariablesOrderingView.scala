package net.enigma.views

import net.enigma.Utils._
import net.enigma.views.components._

/**
 * @author Jacek Lewandowski
 */
trait VariablesOrderingView extends AbstractView {
  val reorderer = new VariablesReorderer().withSizeFull

  addInfo(instructions)

  content.addComponents(reorderer)
  content.setExpandRatio(reorderer, 1)

  def instructions: String
}
