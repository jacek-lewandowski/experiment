package net.enigma.model

/**
 * @author Jacek Lewandowski
 */
case class Variable(
  id: Int,
  title: String,
  ordinalNumber: Option[Int] = None,
  score: Option[Int] = None
) {

  def withOrdinalNumber(ordinalNumber: Int): Variable =
    copy(ordinalNumber = Some(ordinalNumber))

  def withScore(score: Int): Variable =
    copy(score = Some(score))
}
