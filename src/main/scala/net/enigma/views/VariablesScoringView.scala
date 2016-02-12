package net.enigma.views

import com.vaadin.ui.Alignment

import net.enigma.TextResources
import net.enigma.Utils._
import net.enigma.views.components._

/**
 * @author Jacek Lewandowski
 */
trait VariablesScoringView extends AbstractView {
  val scorer = new VariablesScorer()
      .withWidth("70%")
      .withFullHeight

  addInfo(instructions)

  val score = new ScoreCounter(100, TextResources.Labels.Score)
      .withSizeUndefined

  top.addComponent(score)
  top.setComponentAlignment(score, Alignment.MIDDLE_RIGHT)

  content.addComponents(scorer)
  content.setExpandRatio(scorer, 1)
  content.setComponentAlignment(scorer, Alignment.TOP_CENTER)

  scorer.addValueChangedListener(totalScoreUpdated)

  def instructions: String

  def totalScoreUpdated(totalScore: Int): Unit
}
