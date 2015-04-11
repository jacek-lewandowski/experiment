package net.enigma.views

import com.vaadin.ui._

import net.enigma.Utils._
import net.enigma.views.components.QuestionsContainer

/**
 * @author Jacek Lewandowski
 */
trait SurveyView extends AbstractView {
  val questionsContainer = new QuestionsContainer().withFullHeight.withWidth("70%")

  addInfo(instructions)

  content.addComponent(questionsContainer)
  content.setExpandRatio(questionsContainer, 1)
  content.setComponentAlignment(questionsContainer, Alignment.TOP_CENTER)

  def instructions: String

}
