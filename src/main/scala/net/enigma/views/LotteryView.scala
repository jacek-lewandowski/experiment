package net.enigma.views

import com.vaadin.ui.{Alignment, Label}

import net.enigma.TextResources
import net.enigma.views.components._

/**
 * @author Jacek Lewandowski
 */
trait LotteryView extends AbstractView {

  val lotteryOption = TextResources.Labels.Lottery: String
  val confidenceOption = TextResources.Labels.NotLottery: String

  val selector = new ButtonsSelector(Some(question), lotteryOption, confidenceOption)

  selector.addValueChangedListener {
    case `lotteryOption` ⇒ lotterySelected()
    case `confidenceOption` ⇒ confidenceSelected()
  }

  addInfo(instructions)

  val resultLabel = new Label()

  content.addComponents(selector, resultLabel)
  content.setComponentAlignment(resultLabel, Alignment.TOP_CENTER)
  content.setComponentAlignment(selector, Alignment.MIDDLE_CENTER)

  def lotterySelected(): Unit

  def confidenceSelected(): Unit

  def question: String

  def instructions: String
}
