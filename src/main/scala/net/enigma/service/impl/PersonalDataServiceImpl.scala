package net.enigma.service.impl

import org.json4s.native.Serialization

import net.enigma.App
import net.enigma.db.StageDataDAO.PersonalData
import net.enigma.db.{QuestionDAO, StageDataDAO}
import net.enigma.model.{Answer, Question, StageData}
import net.enigma.service.PersonalDataService

/**
 * @author Jacek Lewandowski
 */
trait PersonalDataServiceImpl extends PersonalDataService {
  val personalDataSet = "personalData"

  import StageDataDAO.PersonalData.formats

  override def loadPersonalDataQuestions(): Seq[Question] = QuestionDAO.getQuestionsSet(personalDataSet)

  override def savePersonalDataAnswers(answers: Seq[Answer]): Unit = {
    for (answer ← answers) {
      val json = Serialization.write(answer)
      savePersonalDataAnswer(answer.questionId, json)
    }
  }

  def savePersonalDataAnswer(idx: Int, json: String): Unit = {
    val userCode = App.currentUser.get
    StageDataDAO.saveStageData(StageData(userCode, PersonalData.stageID, PersonalData.answerID, idx, json))
  }

}
