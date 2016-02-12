package net.enigma.service.impl

import org.scalatest.{FunSpec, Matchers}

import net.enigma.model._

/**
 * @author Jacek Lewandowski
 */
class VariablesStageServiceImplTest extends FunSpec with Matchers {

  class VariablesStageServiceImplMock extends VariablesStageServiceImpl("test", VariablesSetup(2)) {
    var stageInfo: Option[String] = None

    override def loadVariablesStageInfo(): Option[String] = stageInfo

    override protected def selectRandomVariables(): List[Variable] = {
      List(
        Variable(1, "a"),
        Variable(2, "b"),
        Variable(3, "c"),
        Variable(4, "d"),
        Variable(5, "e")
      )
    }

    override def saveVariablesStageInfo(json: String): Unit = stageInfo = Some(json)
  }

  describe("VariablesStageServiceImpl") {
    describe("when in initial state") {
      it("should allow to prepare variables") {
        val stageService = new VariablesStageServiceImplMock()
        stageService.prepareVariables()
        val variables = stageService.getVariablesForSelection()
        variables should have size 5
      }

      it("should not allow to do anything but preparing variables") {
        val stageService = new VariablesStageServiceImplMock()
        intercept[IllegalStateException](stageService.setSelectedVariables(List(Variable(1, "a"))))
        intercept[IllegalStateException](stageService.setReorderedVariables(List(Variable(1, "a"))))
        intercept[IllegalStateException](stageService.setScoredVariables(List(Variable(1, "a"))))
        intercept[IllegalStateException](stageService.getVariablesForSelection())
        intercept[IllegalStateException](stageService.getVariablesForReordering())
        intercept[IllegalStateException](stageService.getVariablesForScoring())
        intercept[IllegalStateException](stageService.getVariablesForExperiment())
      }
    }
  }
}
