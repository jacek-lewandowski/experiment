package net.enigma.presenter

import scala.util.{Success, Try}

import com.vaadin.data.validator.EmailValidator
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Notification

import net.enigma.Utils._
import net.enigma.service.{MissingVariablesStageService, UserService}
import net.enigma.views.{MissingVariablesQuestionView, OpenQuestionView}
import net.enigma.{App, TextResources}

/**
 * @author Jacek Lewandowski
 */
trait MissingVariablesQuestionPresenter extends FlowPresenter {
  self: MissingVariablesQuestionView ⇒

  def missingVariablesStageService: MissingVariablesStageService

  override def entered(event: ViewChangeEvent): Unit = {
    App.service.setCurrentStage(App.Views.MissingVariables.name)
    answerField.setValue("")
  }

  override def question: String = TextResources.Labels.MissingVariables

  override def accept(): Boolean = {
    Try(answerField.validate()) match {
      case Success(_) ⇒
        missingVariablesStageService.setMissingVariables(answerField.getValue)
        App.service.completeStage(App.Views.MissingVariables.name)
        true
      case _ ⇒
        Notification.show(TextResources.Notifications.InvalidMissingVariables)
        false
    }
  }

}
