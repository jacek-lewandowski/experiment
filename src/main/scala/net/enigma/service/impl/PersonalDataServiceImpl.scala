package net.enigma.service.impl

import net.enigma.db.QuestionDAO
import net.enigma.model.{Answer, Question}
import net.enigma.service.PersonalDataService

/**
 * @author Jacek Lewandowski
 */
trait PersonalDataServiceImpl extends PersonalDataService {
  val personalDataSet = "personalData"

  override def loadPersonalDataQuestions(): Seq[Question] = QuestionDAO.getQuestionsSet(personalDataSet)

  override def savePersonalDataAnswers(answers: Seq[Answer]): Unit = {}
}
