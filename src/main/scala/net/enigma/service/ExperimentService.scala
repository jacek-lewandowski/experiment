package net.enigma.service

import net.enigma.model.{VariablesSetup, TrialSetup}

/**
 * @author Jacek Lewandowski
 */
trait ExperimentService {
  def getTrialStageService: TrialStageService

  def getVariablesStageService: VariablesStageService

  def getLotteryStageService: LotteryStageService

  def getJustificationsStageService: JustificationsStageService

  def getTrialSetup: TrialSetup

  def getVariablesSetup: VariablesSetup
}
