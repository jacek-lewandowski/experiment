package net.enigma

import com.vaadin.annotations.Theme
import com.vaadin.navigator.Navigator
import com.vaadin.server.VaadinRequest
import com.vaadin.ui._
import org.slf4j.LoggerFactory

@Theme("experiment")
class ExperimentUI extends UI {

  val logger = LoggerFactory.getLogger(classOf[ExperimentUI])

  val navigator = new Navigator(this, this)
  navigator.addProvider(App.Views)
  setNavigator(navigator)

  override def init(request: VaadinRequest): Unit = {}
}
