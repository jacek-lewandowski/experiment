package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent

import net.enigma.App
import net.enigma.model._
import net.enigma.service.VariablesStageService
import net.enigma.views.VariablesOrderingView

/**
 * @author Jacek Lewandowski
 */
trait VariablesOrderingPresenter extends FlowPresenter {
  self: VariablesOrderingView ⇒

  def stageService: VariablesStageService

  def getVariablesForReordering: Seq[Variable] = {
    stageService.getVariablesForReordering()
  }

  def setReorderedVariables(reorderedVariables: List[Variable]) {
    stageService.setReorderedVariables(reorderedVariables)
  }

  override def entered(event: ViewChangeEvent): Unit = {
    reorderer.setVariables(getVariablesForReordering)
    App.service.setCurrentStage(id)
  }

  override def accept(): Boolean = {
    setReorderedVariables(reorderer.getVariables)
    App.service.completeStage(id)
    true
  }

}
