package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent

import net.enigma.App
import net.enigma.views.InstructionView

/**
 * @author Jacek Lewandowski
 */
trait InstructionPresenter extends FlowPresenter {
  self: InstructionView ⇒

  override def instructions: String

  override def accept(): Boolean = {
    App.service.completeStage(id)
    true
  }

  override def entered(event: ViewChangeEvent): Unit = {
    instructionField.setValue(instructions)
    instructionField.setReadOnly(true)
    App.service.setCurrentStage(id)
  }

}
