package net.enigma.views.components

import scala.util.Random

import com.vaadin.ui.{Label, Button, HorizontalLayout, Panel}

import net.enigma.Utils._
import net.enigma.ValueChangedListenable

/**
 * @author Jacek Lewandowski
 */
class ButtonsSelector[T <: AnyRef](text: Option[String], options: T*) extends Panel with ValueChangedListenable[T] {

  val buttons = options.map(option ⇒ new Button(option.toString).withData(option).withFullWidth)

  private val components = text.map(new Label(_)).toList ::: Random.shuffle(buttons).toList
  setContent(new HorizontalLayout(components: _*).withSpacing.withMargins)

  val SelectedStyleName = "two-option-question-button-selected"

  for (button ← buttons) {
    button.addClickListener({ event: Button.ClickEvent ⇒
      button.addStyleName(SelectedStyleName)
      notifyListeners(button.getData.asInstanceOf[T])
    })
  }

  def resetSelection(): Unit = {
    buttons.foreach(_.withRemovedStyleName(SelectedStyleName))
  }

  override def setEnabled(enabled: Boolean): Unit = {
    buttons.foreach(_.setEnabled(enabled))
  }

}
