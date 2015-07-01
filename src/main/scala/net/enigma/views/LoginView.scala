package net.enigma.views

import scala.languageFeature.implicitConversions

import com.vaadin.ui._
import com.wcs.wcslib.vaadin.widget.recaptcha.ReCaptcha
import com.wcs.wcslib.vaadin.widget.recaptcha.shared.ReCaptchaOptions
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

  val publicKey = "6LfOKAkTAAAAAEviQmJ8Rv1V_Y-HBuHs_LmOBqrg"
  val privateKey = "6LfOKAkTAAAAAJQuvrgNMxZhqut8v478XOHeXMvh"

  val captcha = new ReCaptcha(privateKey, publicKey, {
    val opts = new ReCaptchaOptions()
    opts.theme = "white"
    opts.lang = "pl"
    opts
  }).withSizeUndefined
  val panelLayout = new VerticalLayout(codeField, captcha, loginButton).withSpacing.withMargins
  loginPanel.setContent(panelLayout)

  content.addComponent(loginPanel)
  content.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER)

  layout.removeComponent(top)
  layout.removeComponent(bottom)
}
