package net.enigma.presenter

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification
import com.vaadin.ui.Notification.Type
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory

import net.enigma.{App, TextResources, views}

/**
 * @author Jacek Lewandowski
 */
trait LoginPresenter extends FlowPresenter {
  self: views.LoginView ⇒

  private val logger = LoggerFactory.getLogger(classOf[LoginPresenter])

  override def entered(event: ViewChangeEvent): Unit = {
    logger.info(s"Parameters string is: ${event.getParameters}")
    App.service.logout()
    if (StringUtils.isNotBlank(event.getParameters)) {
      if (event.getParameters.trim == App.ADMIN_USER) {
        App.admin = true
        navigateTo(App.Views.Admin.name)
      } else {
        App.admin = false
        codeField.setValue(event.getParameters.trim)
        codeField.setVisible(false)
      }
    } else {
      App.admin = false
      codeField.setValue("")
    }
  }

  override def accept(): Boolean = {
    login(codeField.getValue)
  }

  private def login(userName: String): Boolean = {
    if (captcha.validate()) {
      App.captchaProvided = true
      captcha.setVisible(false)
      App.service.authenticate(userName)
      if (App.currentUser.isDefined) {
        Notification.show(s"${TextResources.Notifications.LoginSuccessful: String}: $userName")
        true
      } else {
        Notification.show(TextResources.Notifications.LoginFailed, Type.ERROR_MESSAGE)
        codeField.setValue("")
        codeField.focus()
        codeField.setVisible(true)
        false
      }
    } else {
      false
    }
  }
}
