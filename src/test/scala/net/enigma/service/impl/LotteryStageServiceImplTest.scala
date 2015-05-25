package net.enigma.service.impl

import org.scalatest.{FunSpec, Matchers}

import net.enigma.model.TrialAnswer.TrialAnswerType
import net.enigma.model._
import net.enigma.service.TrialStageService

/**
 * @author Jacek Lewandowski
 */
class LotteryStageServiceImplTest extends FunSpec with Matchers {

  class LotteryStageServiceImplMock(allowedToStart: Boolean = true, selectedAnswer: TrialAnswerType = TrialAnswer.Plus) extends LotteryStageServiceImpl("test", 2) {
    override lazy val trialStageService: TrialStageService = null

    var lotteryStageInfo: Option[String] = None

    override def saveLotteryInfo(json: String): Unit = lotteryStageInfo = Some(json)

    override def loadLotteryStageInfo(): Option[String] = lotteryStageInfo

    override def getLastIterations(): List[Iteration] = List(
      Iteration(idx = 3, sequence = List(TrialAnswer.Minus, TrialAnswer.Plus), selectedAnswer = Some(selectedAnswer), confidence = Some(50)),
      Iteration(idx = 4, sequence = List(TrialAnswer.Plus, TrialAnswer.Plus), selectedAnswer = Some(selectedAnswer), confidence = Some(100))
    )

    override def isAllowedToStart: Boolean = allowedToStart
  }

  describe("LotteryStageServiceImpl") {
    describe("when in initial state") {
      it("should allow to get the win chance") {
        val stageService = new LotteryStageServiceImplMock(true)
        stageService.isStageCompleted shouldBe false
        stageService.getLotteryWinChance shouldBe 75
        stageService.isStageCompleted shouldBe false
      }

      it("should allow to choose confidence and return true when the answer was correct") {
        val stageService = new LotteryStageServiceImplMock(true)
        stageService.isStageCompleted shouldBe false
        stageService.confidence() shouldBe true
        stageService.isStageCompleted shouldBe true
        intercept[IllegalStateException](stageService.confidence())
        intercept[IllegalStateException](stageService.lottery())
      }

      it("should allow to choose confidence and return false when the answer was incorrect") {
        val stageService = new LotteryStageServiceImplMock(true, TrialAnswer.Minus)
        stageService.isStageCompleted shouldBe false
        stageService.confidence() shouldBe false
        stageService.isStageCompleted shouldBe true
        intercept[IllegalStateException](stageService.confidence())
        intercept[IllegalStateException](stageService.lottery())
      }

      it("should allow to choose lottery") {
        val results = for (i ← 1 to 100) yield {
          val stageService = new LotteryStageServiceImplMock(true)
          stageService.isStageCompleted shouldBe false
          val result = stageService.lottery()
          stageService.isStageCompleted shouldBe true
          intercept[IllegalStateException](stageService.confidence())
          intercept[IllegalStateException](stageService.lottery())
          result
        }
        results.count(x ⇒ x) should be > 60
        results.count(x ⇒ x) should be < 90
      }
    }

    describe("when the previous stage is not finished") {
      it("should not allow to get lottery win chance") {
        val stageService = new LotteryStageServiceImplMock(false)
        intercept[IllegalStateException](stageService.getLotteryWinChance)
      }

      it("should not allow to choose confidence") {
        val stageService = new LotteryStageServiceImplMock(false)
        intercept[IllegalStateException](stageService.confidence())
      }

      it("should not allow to choose lottery") {
        val stageService = new LotteryStageServiceImplMock(false)
        intercept[IllegalStateException](stageService.lottery())
      }
    }
  }

}
