package net.enigma.model

/**
 * @author Jacek Lewandowski
 */
case class QuestionList(id: String, version: Int, notes: String, questions: List[Question])
