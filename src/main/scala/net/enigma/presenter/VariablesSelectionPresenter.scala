package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification

import net.enigma.model._
import net.enigma.service.VariablesStageService
import net.enigma.views.VariablesSelectionView
import net.enigma.{App, TextResources}

/**
 * @author Jacek Lewandowski
 */
trait VariablesSelectionPresenter extends FlowPresenter {
  self: VariablesSelectionView ⇒

  def stageService: VariablesStageService

  lazy val setup = stageService.variablesSetup

  def getInitialVariablesSet: Seq[Variable] = {
    stageService.prepareVariables()
    stageService.getVariablesForSelection()
  }

  def setSelectedVariables(selectedVariables: List[Variable]) {
    stageService.setSelectedVariables(selectedVariables)
  }

  override def entered(event: ViewChangeEvent): Unit = {
    grid.setVariables(getInitialVariablesSet)
    App.service.setCurrentStage(id)
  }

  override def accept(): Boolean = {
    val selectedVariables = grid.getVariables
    if (selectedVariables.size != setup.variablesCount) {
      Notification.show(
        TextResources.Notifications.MustSelectExactlyNVariables.format(setup.variablesCount),
        Notification.Type.HUMANIZED_MESSAGE)
      false
    } else {
      setSelectedVariables(selectedVariables)
      App.service.completeStage(id)
      true
    }
  }

}
