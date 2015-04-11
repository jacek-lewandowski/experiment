package net.enigma.service

import net.enigma.model.TrialSetup

/**
 * @author Jacek Lewandowski
 */
trait ExperimentService {
  def getTrialStageService: TrialStageService

  def getVariablesStageService: VariablesStageService

  def getLotteryStageService: LotteryStageService

  def getTrialSetup: TrialSetup
}
