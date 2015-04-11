package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification
import com.vaadin.ui.Notification.Type

import net.enigma.{App, TextResources, views}

/**
 * @author Jacek Lewandowski
 */
trait LoginPresenter extends FlowPresenter {
  self: views.LoginView ⇒

  override def entered(event: ViewChangeEvent): Unit = {
    App.service.logout()
  }

  override def accept(): Boolean = {
    App.service.authenticate(codeField.getValue)

    if (App.currentUser.exists(_.isAbleToProceed)) {
      Notification.show(TextResources.Notifications.LoginSuccessful)
      true
    } else {
      Notification.show(TextResources.Notifications.LoginFailed, Type.ERROR_MESSAGE)
      codeField.setValue("")
      codeField.focus()
      false
    }
  }
}
