package net.enigma.service.impl

import org.slf4j.LoggerFactory

import net.enigma.model.TrialSetup
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
    val sequenceSetup = ts.Sequences
    val sequenceLength = ts.SequenceLength.toInt

    TrialSetup(
      minSelectedVarsCount,
      maxSelectedVarsCount,
      totalScore,
      unitPrice,
      essentialVarsCount,
      sequenceSetup,
      sequenceLength
    )
  }

}