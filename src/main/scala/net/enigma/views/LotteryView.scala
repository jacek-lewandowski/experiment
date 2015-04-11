package net.enigma.views

import com.vaadin.ui.{Alignment, Label}

import net.enigma.TextResources
import net.enigma.views.components._

/**
 * @author Jacek Lewandowski
 */
trait LotteryView extends AbstractView {
  val questionLabel = new Label(question)

  val lotteryOption = TextResources.Labels.Lottery: String
  val confidenceOption = TextResources.Labels.NotLottery: String

  val selector = new ButtonsSelector(lotteryOption, confidenceOption)

  selector.addValueChangedListener {
    case `lotteryOption` ⇒ lotterySelected()
    case `confidenceOption` ⇒ confidenceSelected()
  }

  addInfo(instructions)

  content.addComponents(questionLabel, selector)
  content.setComponentAlignment(questionLabel, Alignment.TOP_CENTER)
  content.setComponentAlignment(selector, Alignment.MIDDLE_CENTER)

  def lotterySelected(): Unit

  def confidenceSelected(): Unit

  def question: String

  def instructions: String
}
