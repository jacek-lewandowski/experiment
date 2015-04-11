package net.enigma.service.impl

import net.enigma.db.QuestionDAO
import net.enigma.model.{Answer, Question}
import net.enigma.service.QuestionnaireService

/**
 * @author Jacek Lewandowski
 */
trait QuestionnaireServiceImpl extends QuestionnaireService {
  val questionnaireSet = "questionnaire"

  override def saveQuestionnaireAnswers(answers: Seq[Answer]): Unit = {}

  override def loadQuestionnaireQuestions(): Seq[Question] = QuestionDAO.getQuestionsSet(questionnaireSet)
}
