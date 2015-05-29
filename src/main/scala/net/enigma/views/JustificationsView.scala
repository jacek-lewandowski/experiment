package net.enigma.views

import com.vaadin.ui.{Alignment, Label, TextField}

import net.enigma.Utils._
import net.enigma.views.components.{JustificationsGrid, VariablesGrid}

/**
 * @author Jacek Lewandowski
 */
trait JustificationsView extends AbstractView {
  val grid = new JustificationsGrid().withSizeFull

  addInfo(instructions)

  content.addComponents(grid)
  content.setExpandRatio(grid, 1)

  def instructions: String
}
