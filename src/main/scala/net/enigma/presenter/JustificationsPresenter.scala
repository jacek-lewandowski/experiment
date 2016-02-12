package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent

import net.enigma.App
import net.enigma.model.Variable
import net.enigma.service.JustificationsStageService
import net.enigma.views.JustificationsView

/**
 * @author Jacek Lewandowski
 */
trait JustificationsPresenter extends FlowPresenter {
  self: JustificationsView ⇒

  def stageService: JustificationsStageService

  def getInitialVariablesSet: Seq[Variable] = {
    stageService.getVariablesToJustify()
  }

  def setJustifiedVariables(variables: List[Variable]) {
    stageService.setJustifiedVariables(variables)
  }

  override def entered(event: ViewChangeEvent): Unit = {
    grid.setVariables(getInitialVariablesSet)
    App.service.setCurrentStage(id)
  }

  override def accept(): Boolean = {
    val vars = grid.getVariables
    setJustifiedVariables(vars)
    App.service.completeStage(id)
    true
  }
}
