package net.enigma

import com.vaadin.annotations.{JavaScript, Theme}
import com.vaadin.navigator.Navigator
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.UI
import org.slf4j.LoggerFactory

@Theme("experiment")
@JavaScript(value = Array("http://www.google.com/recaptcha/api/js/recaptcha_ajax.js"))
class ExperimentUI extends UI {

  val logger = LoggerFactory.getLogger(classOf[ExperimentUI])

  val navigator = new Navigator(this, this)
  navigator.addProvider(App.Views)
  setNavigator(navigator)

  override def init(request: VaadinRequest): Unit = {}
}
