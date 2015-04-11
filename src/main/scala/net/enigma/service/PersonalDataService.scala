package net.enigma.service

import net.enigma.model.{Answer, Question}

/**
 * @author Jacek Lewandowski
 */
trait PersonalDataService {
  def loadPersonalDataQuestions(): Seq[Question]

  def savePersonalDataAnswers(answers: Seq[Answer]): Unit

}
