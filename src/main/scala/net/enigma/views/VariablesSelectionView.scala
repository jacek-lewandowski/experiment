package net.enigma.views

import net.enigma.Utils._
import net.enigma.views.components._

/**
 * @author Jacek Lewandowski
 */
trait VariablesSelectionView extends AbstractView {
  val grid = new VariablesGrid().withSizeFull

  addInfo(instructions)

  content.addComponents(grid)
  content.setExpandRatio(grid, 1)

  def instructions: String
}
