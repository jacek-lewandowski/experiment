package net.enigma

object NeedlemanWunsch {

  val gapPenalty = 0

  def score(seqA: CharSequence, seqB: CharSequence): Int = {
    def substitutionPenalty(i: Int, j: Int) = if (seqA.charAt(i - 1) == seqB.charAt(j - 1)) 1 else -1

    val mD = Array.fill(seqA.length() + 1, seqB.length() + 1)(0)
    for (i <- 0 to seqA.length(); j <- 0 to seqB.length())
      if (i == 0) mD(i)(j) = -j else if (j == 0) mD(i)(j) = -i

    for (i <- 1 to seqA.length(); j <- 1 to seqB.length()) {
      val scoreDiag = mD(i - 1)(j - 1) + substitutionPenalty(i, j)
      val scoreLeft = mD(i)(j - 1) - gapPenalty
      val scoreUp = mD(i - 1)(j) - gapPenalty
      mD(i)(j) = math.max(math.max(scoreDiag, scoreLeft), scoreUp)
    }

    mD(seqA.length())(seqB.length())
  }

}
