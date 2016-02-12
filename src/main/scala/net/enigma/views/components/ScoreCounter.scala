package net.enigma.views.components

import com.vaadin.ui.Label

/**
 * @author Jacek Lewandowski
 */
class ScoreCounter(val initialValue: Int, caption: String) extends Label {
  setCaption(caption)
  setValue(initialValue.toString)
}
