package net.enigma.views

import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._

import net.enigma.Utils._

/**
 * @author Jacek Lewandowski
 */
trait InstructionView extends AbstractView {
  val instructionField = new Label(instructions)
    .withFullWidth
    .withContentMode(ContentMode.HTML)
  val contentPanel = new Panel(new VerticalLayout(instructionField).withSpacing.withMargins)
    .withSizeFull

  addTitle()
  content.addComponent(contentPanel)
  content.setComponentAlignment(contentPanel, Alignment.TOP_CENTER)
  content.setExpandRatio(contentPanel, 1)

  def instructions: String

}
