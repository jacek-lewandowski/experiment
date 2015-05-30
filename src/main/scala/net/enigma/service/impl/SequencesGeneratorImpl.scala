package net.enigma.service.impl

import net.enigma.App
import net.enigma.model.TrialAnswer.TrialAnswerType
import net.enigma.model.{TrialAnswer, TrialSetup}
import net.enigma.service.impl.SequencesGeneratorImpl.Sequence

/**
 * @author Jacek Lewandowski
 */
class SequencesGeneratorImpl(trialSetup: TrialSetup) {

  def generateSequences(): List[List[TrialAnswerType]] = {
    val seqGroups = parseSetup(trialSetup.sequenceSetup)
    val shuffledSeqGroups = seqGroups.map(App.random.shuffle(_))
    val clearSeqSource = makeClearSequenceSource(trialSetup.maxSelectedVariablesCount)
    val ambiguousSeqSource = makeAmbiguousSequenceSource(trialSetup.sequenceLength)
    generateSequences(shuffledSeqGroups, clearSeqSource, ambiguousSeqSource)
  }

  /**
   * Parses setup and generates a list of sequences groups. The final outcome which is to be presented
   * to the user is a flattened list of sequences.
   */
  private def parseSetup(setup: String): List[List[Sequence.Value]] = {
    val groups = for (shuffleRange ← setup.split('|').toList if shuffleRange.nonEmpty) yield {
      shuffleRange.toUpperCase.toList.collect {
        case 'A' ⇒ Sequence.ambiguous
        case 'C' ⇒ Sequence.clear
      }
    }

    groups.filter(_.nonEmpty)
  }

  private def generateSequences(
    sequencesGroups: List[List[Sequence.Value]],
    clearSequenceSource: Iterator[List[TrialAnswerType]],
    ambiguousSequenceSource: Iterator[List[TrialAnswerType]]
  ): List[List[TrialAnswerType]] = {
    for (sequenceGroup ← sequencesGroups; sequence ← sequenceGroup) yield {
      sequence match {
        case Sequence.clear ⇒ clearSequenceSource.next()
        case Sequence.ambiguous ⇒ ambiguousSequenceSource.next()
      }
    }
  }

  private def makeClearSequenceSource(sequenceLength: Int): Iterator[List[TrialAnswerType]] = {
    require(sequenceLength > 0)

    val fullSeqSet = Seq(TrialAnswer.Minus, TrialAnswer.Plus).map(trialAnswer ⇒
      Iterator.continually(trialAnswer).take(sequenceLength).toList)

    Iterator.continually(App.random.shuffle(fullSeqSet)).flatten
  }

  private def makeAmbiguousSequenceSource(sequenceLength: Int): Iterator[List[TrialAnswerType]] = {
    require(sequenceLength > 0)
    require(sequenceLength < 10)

    val fullSeqSet = for (plusesNumber ← 1 to sequenceLength - 1;
         combination ← (0 until sequenceLength).combinations(plusesNumber).toList)
      yield {
        val f = combination.map(idx ⇒ (idx, TrialAnswer.Plus)).toMap.withDefaultValue(TrialAnswer.Minus)
        (0 until sequenceLength).map(f).toList
      }

    Iterator.continually(App.random.shuffle(fullSeqSet)).flatten
  }

}

object SequencesGeneratorImpl {
  type SequenceType = Sequence.type

  object Sequence extends Enumeration {
    val clear = Value(0, "clear")
    val ambiguous = Value(1, "ambiguous")
  }

}
