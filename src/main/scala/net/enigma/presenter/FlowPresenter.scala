package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent

import net.enigma.App
import net.enigma.App.Views.Login
import net.enigma.views.AbstractView

/**
 * @author Jacek Lewandowski
 */
trait FlowPresenter {
  self: AbstractView ⇒

  def nextView: String

  def id: String

  def title: String

  def accept(): Boolean

  def next(): Unit = {
    if (accept()) {
      nextButton.setEnabled(false)
      navigateTo(nextView)
    }
  }

  override def failedToEnter(event: ViewChangeEvent): Unit = {
    App.service.getCurrentStage() match {
      case Some(stage) ⇒ navigateTo(stage)
      case _ ⇒ navigateTo(App.Views.findAllowedProvider())
    }
  }
}
