package net.enigma.views

import com.vaadin.ui.Alignment

import net.enigma.Utils._
import net.enigma.views.components._

/**
 * @author Jacek Lewandowski
 */
trait VariablesOrderingView extends AbstractView {
  val reorderer = new VariablesReorderer()
      .withWidth("70%")
      .withFullHeight

  addInfo(instructions)

  content.addComponents(reorderer)
  content.setExpandRatio(reorderer, 1)
  content.setComponentAlignment(reorderer, Alignment.TOP_CENTER)

  def instructions: String
}
