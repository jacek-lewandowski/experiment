package net.enigma.views

import com.vaadin.ui.{TextArea, Alignment, Label, TextField}

import net.enigma.Utils._

/**
 * @author Jacek Lewandowski
 */
trait MissingVariablesQuestionView extends AbstractView {
  val questionLabel = new Label(question).withWidth("70%")
  val answerField = new TextArea().withWidth("70%").withHeight("50%").withMaxLength(2000)

  content.addComponents(questionLabel, answerField)
  content.setComponentAlignment(questionLabel, Alignment.MIDDLE_CENTER)
  content.setComponentAlignment(answerField, Alignment.MIDDLE_CENTER)

  def question: String

}
