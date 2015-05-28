package net.enigma.presenter

import scala.util.{Success, Try}

import com.vaadin.data.validator.EmailValidator
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification

import net.enigma.{App, TextResources}
import net.enigma.Utils._
import net.enigma.service.UserService
import net.enigma.views.OpenQuestionView

/**
 * @author Jacek Lewandowski
 */
trait EmailPresenter extends FlowPresenter {
  self: OpenQuestionView ⇒

  def userService: UserService

  override def entered(event: ViewChangeEvent): Unit = {
    userService.setCurrentStage(App.Views.EmailAddress.name)
    answerField
        .withBlurListener(_ ⇒ Try(answerField.validate()))
        .withFocusListener(_ ⇒ answerField.selectAll())
        .withAdditionalValidator(new EmailValidator(TextResources.Notifications.InvalidEmailAddress))
        .setValue("")
  }

  override def question: String = TextResources.Labels.EmailQuestion

  override def accept(): Boolean = {
    Try(answerField.validate()) match {
      case Success(_) ⇒
        userService.setEmailAddress(answerField.getValue)
        userService.completeStage(App.Views.EmailAddress.name)
        true
      case _ ⇒
        Notification.show(TextResources.Notifications.InvalidEmailAddress)
        false
    }
  }

}
