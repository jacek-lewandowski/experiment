package net.enigma.views

import scala.languageFeature.implicitConversions

import com.vaadin.ui._
import org.vaadin.addons.rinne.events.ButtonClickListener

import net.enigma.TextResources
import net.enigma.Utils._

/**
 * @author Jacek Lewandowski
 */
trait LoginView extends AbstractView {
  val codeField = new TextField(TextResources.Labels.LoginCodeField)
  val loginButton = new Button(TextResources.Labels.LoginButton, new ButtonClickListener(_ ⇒ next()))
  val loginPanel = new Panel(TextResources.Titles.Login).withSizeUndefined
  val panelLayout = new VerticalLayout(codeField, loginButton).withSpacing.withMargins
  loginPanel.setContent(panelLayout)

  content.addComponent(loginPanel)
  content.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER)

  layout.removeComponent(top)
  layout.removeComponent(bottom)
}
