package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import net.enigma.service.TrialStageService
import net.enigma.views.VariablesSelectionView

/**
 * @author Jacek Lewandowski
 */
trait MostImportantVariablesPresenter extends FlowPresenter {
  self: VariablesSelectionView ⇒

  def stageService: TrialStageService

  override def accept(): Boolean = {
    val variables = grid.getVariables
    stageService.setEssentialVariables(variables)
    stageService.isMostImportantVariablesProvided
  }

  override def entered(event: ViewChangeEvent): Unit = {
    val variables = stageService.getSelectedVariables()
    grid.setVariables(variables)
  }
}
