package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification

import net.enigma.TextResources
import net.enigma.service.TrialStageService
import net.enigma.views.VariablesSelectionView

/**
 * @author Jacek Lewandowski
 */
trait MostImportantVariablesPresenter extends FlowPresenter {
  self: VariablesSelectionView ⇒

  def stageService: TrialStageService

  override def accept(): Boolean = {
    val trialSetup = stageService.trialSetup
    val selectedVariables = grid.getVariables
    if (selectedVariables.size != trialSetup.essentialVarsCount) {
      Notification.show(
        TextResources.Notifications.MustSelectExactlyNEssentialVariables.format(trialSetup.essentialVarsCount),
        Notification.Type.HUMANIZED_MESSAGE)
      false
    } else {
      stageService.setEssentialVariables(selectedVariables)
      stageService.isMostImportantVariablesProvided
      true
    }
  }

  override def entered(event: ViewChangeEvent): Unit = {
    val variables = stageService.getSelectedVariables()
    grid.setVariables(variables)
  }
}
