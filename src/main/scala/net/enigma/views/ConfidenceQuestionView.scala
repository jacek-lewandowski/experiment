package net.enigma.views

import com.vaadin.ui._

import net.enigma.Utils._

/**
 * @author Jacek Lewandowski
 */
trait ConfidenceQuestionView extends AbstractView {
  val answerSlider = new Slider(question, 50, 100)
      .withFullWidth
      .withImmediate

  answerSlider.setValue(50d)
  answerSlider.setResolution(0)

  addInfo(instructions)

  private val leftLabel = new Label("50%").withSizeUndefined
  private val rightLabel = new Label("100%").withSizeUndefined
  private val sliderLayout = new HorizontalLayout(leftLabel, answerSlider, rightLabel)
  sliderLayout
      .withWidth("70%")
      .withExpandRatio(answerSlider, 1)
      .withSpacing
      .withComponentAlignment(leftLabel, Alignment.BOTTOM_LEFT)
      .withComponentAlignment(rightLabel, Alignment.BOTTOM_RIGHT)

  content.addComponents(sliderLayout)
  content.setComponentAlignment(sliderLayout, Alignment.MIDDLE_CENTER)

  def question: String

  def instructions: String

}
