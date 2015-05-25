package net.enigma.service.impl

import org.json4s.native.Serialization

import net.enigma.App
import net.enigma.db.StageDataDAO.Questionnnaire
import net.enigma.db.{QuestionDAO, StageDataDAO}
import net.enigma.model.{Answer, Question, StageData}
import net.enigma.service.QuestionnaireService

/**
 * @author Jacek Lewandowski
 */
trait QuestionnaireServiceImpl extends QuestionnaireService {
  val questionnaireSet = "questionnaire"

  import StageDataDAO.Questionnnaire.formats

  override def saveQuestionnaireAnswers(answers: Seq[Answer]): Unit = {
    for (answer ← answers) {
      val json = Serialization.write(answer)
      saveQuestionnaireAnswer(answer.questionId, json)
    }
  }

  def saveQuestionnaireAnswer(idx: Int, json: String): Unit = {
    val userCode = App.currentUser.get.code
    StageDataDAO.saveStageData(StageData(userCode, Questionnnaire.stageID, Questionnnaire.answerID, idx, json))
  }

  override def loadQuestionnaireQuestions(): Seq[Question] =
    QuestionDAO.getQuestionsSet(questionnaireSet)
}
