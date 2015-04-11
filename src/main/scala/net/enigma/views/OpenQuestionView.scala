package net.enigma.views

import com.vaadin.ui.{Alignment, Label, TextField}

import net.enigma.Utils._

/**
 * @author Jacek Lewandowski
 */
trait OpenQuestionView extends AbstractView {
  val questionLabel = new Label(question).withWidth("70%")
  val answerField = new TextField().withWidth("70%")

  addInfo(instructions)

  content.addComponents(questionLabel, answerField)
  content.setComponentAlignment(questionLabel, Alignment.MIDDLE_CENTER)
  content.setComponentAlignment(answerField, Alignment.MIDDLE_CENTER)

  def question: String

  def instructions: String

}
