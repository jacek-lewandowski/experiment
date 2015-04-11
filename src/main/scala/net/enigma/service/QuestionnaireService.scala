package net.enigma.service

import net.enigma.model.{Answer, Question}

/**
 * @author Jacek Lewandowski
 */
trait QuestionnaireService {
  def saveQuestionnaireAnswers(answers: Seq[Answer]): Unit

  def loadQuestionnaireQuestions(): Seq[Question]

}
