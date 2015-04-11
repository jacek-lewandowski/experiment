package net.enigma.service.impl

import scala.util.{Failure, Success, Try}

import org.slf4j.LoggerFactory

import net.enigma.model.{SequenceSetup, ShuffleRange, TrialAnswer, TrialSetup}
import net.enigma.service.{ExperimentService, LotteryStageService, TrialStageService, VariablesStageService}
import net.enigma.{App, TextResources}

/**
 * @author Jacek Lewandowski
 */
trait ExperimentServiceImpl extends ExperimentService {
  private val logger = LoggerFactory.getLogger(classOf[ExperimentServiceImpl])

  override def getTrialStageService: TrialStageService = {
    new TrialStageServiceImpl(App.currentUser.get.code, getTrialSetup)
  }

  override def getVariablesStageService: VariablesStageService = {
    new VariablesStageServiceImpl(App.currentUser.get.code)
  }

  override def getLotteryStageService: LotteryStageService = {
    new LotteryStageServiceImpl(App.currentUser.get.code)
  }

  override def getTrialSetup: TrialSetup = {
    val ts = TextResources.Setup.Trial

    val minSelectedVarsCount = ts.MinSelectedVariables.toInt
    val maxSelectedVarsCount = ts.MaxSelectedVariables.toInt
    val totalScore = ts.TotalScore.toInt
    val unitPrice = ts.UnitPrice.toInt
    val essentialVarsCount = ts.EssentialVariables.toInt
    val sequenceSetup = getSequenceSetup

    TrialSetup(
      minSelectedVarsCount,
      maxSelectedVarsCount,
      totalScore,
      unitPrice,
      essentialVarsCount,
      sequenceSetup
    )
  }

  def getSequenceSetup: SequenceSetup = {
    val ts = TextResources.Setup.Trial

    val sequencesStrings = ts.Sequences.split( """\s*,\s*""").toList
    val sequencesShuffleRangeStrings = ts.ShuffleRange.split( """\s*,\s*""").toList

    val sequences = for (sequence ← sequencesStrings) yield {
      sequence.toList.flatMap {
        case '+' ⇒ Some(TrialAnswer.Plus)
        case '-' ⇒ Some(TrialAnswer.Minus)
        case x ⇒
          logger.warn(s"Invalid character in sequence: $x")
          None
      }
    }

    val sequencesShuffleRange = sequencesShuffleRangeStrings match {
      case Nil ⇒
        None
      case List(l, r) ⇒
        Try(ShuffleRange(l.toInt - 1, r.toInt)) match {
          case Success(range) ⇒ Some(range)
          case Failure(ex) ⇒
            logger.warn(s"Failed to read shuffle range: $sequencesShuffleRangeStrings, ${ex.toString}")
            None
        }
      case x ⇒
        logger.warn(s"Invalid shuffle range: $x")
        None
    }

    val shuffledSequences = sequencesShuffleRange.map(_.shuffle(sequences)).getOrElse(sequences)

    SequenceSetup(shuffledSequences, sequencesShuffleRange)
  }

}
