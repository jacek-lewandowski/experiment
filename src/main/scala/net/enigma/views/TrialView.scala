package net.enigma.views

import com.vaadin.ui._

import net.enigma.TextResources
import net.enigma.Utils._
import net.enigma.model.TrialAnswer.{Minus, Plus, TrialAnswerType}
import net.enigma.model.{VariableValue, Variable}
import net.enigma.views.components.{ButtonsSelector, ExperimentGrid, ScoreCounter}

/**
 * @author Jacek Lewandowski
 */
trait TrialView extends AbstractView {

  val bullMarket = TextResources.Labels.Plus: String
  val bearMarket = TextResources.Labels.Minus: String

  val grid = new ExperimentGrid(computeNextValue).withSizeFull
  val decisionSelector = new ButtonsSelector(Some(question), bullMarket, bearMarket)
      .withSizeUndefined
  val score = new ScoreCounter(100, TextResources.Labels.Score).withSizeUndefined
  val spacer = new HorizontalLayout()
  val questionLabel = new Label(question)

  top.addComponents(score, spacer, decisionSelector)
  top.setComponentAlignment(score, Alignment.MIDDLE_RIGHT)
  top.setExpandRatio(spacer, 1)

  content.addComponents(grid)
  content.setExpandRatio(grid, 1)

  grid.addValueChangedListener(x ⇒ updateSelectionCount())
  decisionSelector.addValueChangedListener {
    case `bullMarket` ⇒ selectAnswer(Plus)
    case `bearMarket` ⇒ selectAnswer(Minus)
  }

  def instructions: String

  def question: String

  def computeNextValue(selectedVariable: Variable): VariableValue

  def updateSelectionCount(): Unit

  def selectAnswer(answer: TrialAnswerType): Unit

}
