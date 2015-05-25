package net.enigma.service.impl

import scala.collection.mutable

import org.json4s.native.Serialization._
import org.scalatest.{FunSpec, Matchers}

import net.enigma.db.StageDataDAO
import net.enigma.model.TrialStageInfo.IterationState
import net.enigma.model._
import net.enigma.service.TrialStageService

/**
 * @author Jacek Lewandowski
 */
class TrialStageServiceImplTest extends FunSpec with Matchers {
  val trialSetup = TrialSetup(
    minSelectedVariablesCount = 3,
    maxSelectedVariablesCount = 4,
    totalScore = 100,
    unitPrice = 10,
    essentialVarsCount = 2,
    sequenceSetup = "CCAA",
    sequenceLength = 2
  )

  class TrialStageServiceMock(trialSetup: TrialSetup) extends TrialStageServiceImpl("test", trialSetup) {
    val data = mutable.HashMap[AnyRef, String]()

    override def getInitialVariables(): List[VariableDefinition] = {
      List(
        VariableDefinition("real", 1, "a", "aa", "aaa"),
        VariableDefinition("real", 2, "b", "bb", "bbb"),
        VariableDefinition("real", 3, "c", "cc", "ccc"),
        VariableDefinition("real", 4, "d", "dd", "ddd"),
        VariableDefinition("real", 5, "e", "ee", "eee"),
        VariableDefinition("real", 6, "f", "ff", "fff")
      )
    }

    override def saveFinishedIteration(iterationNumber: Int, json: String): Unit = {
      data += (userCode, s"iter-$iterationNumber") → json
    }

    override def saveStageInfo(json: String): Unit = {
      data += (userCode, "trialStageInfo") → json
    }

    override def loadStageInfo(): Option[String] = {
      data.get((userCode, "trialStageInfo"))
    }
  }

  def serviceInInitialState(setup: TrialSetup = trialSetup) = {
    new TrialStageServiceMock(setup)
  }

  def serviceWithStartedIteration(initialService: TrialStageService = serviceInInitialState()) = {
    initialService.prepareVariables()
    initialService
  }

  def serviceWithSelectedVars(initialService: TrialStageService = serviceInInitialState()) = {
    serviceWithStartedIteration(initialService)
    val vars = initialService.getPreparedVariables().take(initialService.trialSetup.maxSelectedVariablesCount)
    for (VariableDefinition(_, id, title, _, _) ← vars) {
      initialService.selectVariable(Variable(id, title))
    }
    initialService
  }

  def serviceWithSelectedAnswer(initialService: TrialStageService = serviceInInitialState()) = {
    serviceWithSelectedVars(initialService)
    initialService.setAnswer(TrialAnswer.Plus)
    initialService
  }

  def serviceWithProvidedConfidence(initialService: TrialStageService = serviceInInitialState()) = {
    serviceWithSelectedAnswer(initialService)
    initialService.setConfidence(75)
    initialService
  }

  def serviceWithProvidedExplanation(initialService: TrialStageService = serviceInInitialState()) = {
    serviceWithProvidedConfidence(initialService)
    initialService.setExplanation("abc")
    initialService
  }

  def serviceWithProvidedEssentialVars(initialService: TrialStageService = serviceInInitialState()) = {
    serviceWithProvidedExplanation(initialService)
    val vars = initialService.getSelectedVariables().take(initialService.trialSetup.essentialVarsCount)
    initialService.setEssentialVariables(vars)
    initialService
  }

  def serviceWithLastIterationFinished(setup: TrialSetup = trialSetup) = {
    val service = serviceInInitialState(setup)
    for (sequence ← service.getStageInfo.sequences) {
      serviceWithProvidedEssentialVars(service)
    }
    service
  }

  describe("Json4s") {
    val stageService = new TrialStageServiceMock(trialSetup)
    implicit val format = StageDataDAO.formats

    describe("when used to serialize / deserialize sequence setup") {
      it("should work when ss is empty") {
        val obj = TrialStageInfo(trialSetup, Nil, None)
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
      it("should work when ss contains empty sequences") {
        val obj = TrialStageInfo(trialSetup, List(Nil, Nil), None)
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
      it("should work when ss contains regular sequences") {
        val obj = TrialStageInfo(trialSetup, List(List(TrialAnswer.Minus, TrialAnswer.Plus), List(TrialAnswer.Plus, TrialAnswer.Minus)), None)
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
      it("should work when ss contains shuffle range") {
        val obj = TrialStageInfo(trialSetup, List(Nil, List(TrialAnswer.Minus, TrialAnswer.Plus)))
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
    }

    describe("when used to serialize / deserialize stage info") {
      it("should work when si has no iteration defined") {
        val obj = TrialStageInfo(trialSetup, Nil, None)
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
      it("should work when si has empty iteration defined") {
        val obj = TrialStageInfo(trialSetup, Nil, Some(Iteration(0, Nil, Nil, Nil, None, None, None, Nil)))
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
      it("should work when si has empty iteration with changed state defined") {
        val obj = TrialStageInfo(trialSetup, Nil, Some(Iteration(0, Nil, Nil, Nil, None, None, None, Nil)), IterationState.finished)
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
      it("should work when si has iteration with some answer selected") {
        val obj = TrialStageInfo(trialSetup, Nil, Some(Iteration(0, Nil, Nil, Nil, Some(TrialAnswer.Plus), None, None, Nil)))
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
      it("should work when si has iteration with some confidence provided") {
        val obj = TrialStageInfo(trialSetup, Nil, Some(Iteration(0, Nil, Nil, Nil, None, Some(1), None, Nil)))
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
      it("should work when si has iteration with some explanation provided") {
        val obj = TrialStageInfo(trialSetup, Nil, Some(Iteration(0, Nil, Nil, Nil, None, None, Some("asdf"), Nil)))
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
      it("should work when si has iteration with some essential variables provided") {
        val obj = TrialStageInfo(trialSetup, Nil, Some(Iteration(0, Nil, Nil, Nil, None, None, None, List(Variable(1, "asdf"), Variable(2, "bcde")))))
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
      it("should work when si has iteration with some sequence defined") {
        val obj = TrialStageInfo(trialSetup, Nil, Some(Iteration(0, List(TrialAnswer.Minus, TrialAnswer.Plus), Nil, Nil, None, None, None, Nil)))
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
      it("should work when si has iteration with some initial variables defined") {
        val obj = TrialStageInfo(trialSetup, Nil, Some(Iteration(0, Nil, List(VariableDefinition("a", 1, "asdf", "sdfg", "dfgh"), VariableDefinition("a", 1, "asdf", "sdfg", "dfgh")), Nil, None, None, None, Nil)))
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
      it("should work when si has iteration with some selected variables provided") {
        val obj = TrialStageInfo(trialSetup, Nil, Some(Iteration(0, Nil, Nil, List(VariableValue(Variable(1, "asdf", None, None), TrialAnswer.Minus, "qwer"), VariableValue(Variable(1, "sdfg", Some(1), None), TrialAnswer.Minus, "erty"), VariableValue(Variable(1, "dfgh", None, Some(2)), TrialAnswer.Minus, "wert")), None, None, None, Nil)))
        val json = write(obj)
        val readObj = read[TrialStageInfo](json)
        readObj shouldBe obj
      }
    }
  }

  describe("TrialStageService flow control") {
    describe("when in initial state") {
      val stageService = serviceInInitialState()

      it("should not allow to do anything but preparing new variables set") {
        intercept[IllegalStateException](stageService.selectVariable(Variable(1, "aaa")))
        intercept[IllegalStateException](stageService.setAnswer(TrialAnswer.Plus))
        intercept[IllegalStateException](stageService.setConfidence(75))
        intercept[IllegalStateException](stageService.setExplanation("explanation"))
        intercept[IllegalStateException](stageService.getSelectedVariables())
        intercept[IllegalStateException](stageService.setEssentialVariables(List(Variable(1, "aaa"))))
      }

      it("should report a valid state") {
        stageService.isAnswerProvided shouldBe false
        stageService.isConfidenceProvided shouldBe false
        stageService.isExplanationProvided shouldBe false
        stageService.isMostImportantVariablesProvided shouldBe false
        stageService.isIterationStarted shouldBe false
        stageService.isIterationFinished shouldBe false
        stageService.isNextIterationAvailable shouldBe true
      }
    }

    describe("when iteration is started") {
      val stageService = serviceWithStartedIteration()

      it("should not allow to do anything but selecting variables") {
        intercept[IllegalStateException](stageService.prepareVariables())
        intercept[IllegalStateException](stageService.setAnswer(TrialAnswer.Plus))
        intercept[IllegalStateException](stageService.setConfidence(75))
        intercept[IllegalStateException](stageService.setExplanation("explanation"))
        intercept[IllegalStateException](stageService.setEssentialVariables(List(Variable(1, "aaa"))))
      }

      it("should report a valid state") {
        stageService.isAnswerProvided shouldBe false
        stageService.isConfidenceProvided shouldBe false
        stageService.isExplanationProvided shouldBe false
        stageService.isMostImportantVariablesProvided shouldBe false
        stageService.isIterationStarted shouldBe true
        stageService.isIterationFinished shouldBe false
        stageService.isNextIterationAvailable shouldBe true
      }
    }

    describe("when answer is selected") {
      val stageService = serviceWithSelectedAnswer()

      it("should not allow to do anything but providing confidence") {
        intercept[IllegalStateException](stageService.prepareVariables())
        intercept[IllegalStateException](stageService.selectVariable(Variable(1, "aaa")))
        intercept[IllegalStateException](stageService.setAnswer(TrialAnswer.Plus))
        intercept[IllegalStateException](stageService.setExplanation("explanation"))
        intercept[IllegalStateException](stageService.setEssentialVariables(List(Variable(1, "aaa"))))
      }

      it("should report a valid state") {
        stageService.isAnswerProvided shouldBe true
        stageService.isConfidenceProvided shouldBe false
        stageService.isExplanationProvided shouldBe false
        stageService.isMostImportantVariablesProvided shouldBe false
        stageService.isIterationStarted shouldBe true
        stageService.isIterationFinished shouldBe false
        stageService.isNextIterationAvailable shouldBe true
      }
    }

    describe("when confidence is provided") {
      val stageService = serviceWithProvidedConfidence()

      it("should not allow to do anything but providing explanation") {
        intercept[IllegalStateException](stageService.prepareVariables())
        intercept[IllegalStateException](stageService.selectVariable(Variable(1, "aaa")))
        intercept[IllegalStateException](stageService.setAnswer(TrialAnswer.Plus))
        intercept[IllegalStateException](stageService.setConfidence(75))
        intercept[IllegalStateException](stageService.setEssentialVariables(List(Variable(1, "aaa"))))
      }

      it("should report a valid state") {
        stageService.isAnswerProvided shouldBe true
        stageService.isConfidenceProvided shouldBe true
        stageService.isExplanationProvided shouldBe false
        stageService.isMostImportantVariablesProvided shouldBe false
        stageService.isIterationStarted shouldBe true
        stageService.isIterationFinished shouldBe false
        stageService.isNextIterationAvailable shouldBe true
      }
    }

    describe("when explanation is provided") {
      val stageService = serviceWithProvidedExplanation()

      it("should not allow to do anything but providing essential variables set") {
        intercept[IllegalStateException](stageService.prepareVariables())
        intercept[IllegalStateException](stageService.selectVariable(Variable(1, "aaa")))
        intercept[IllegalStateException](stageService.setAnswer(TrialAnswer.Plus))
        intercept[IllegalStateException](stageService.setConfidence(75))
        intercept[IllegalStateException](stageService.setExplanation("explanation"))
      }

      it("should report a valid state") {
        stageService.isAnswerProvided shouldBe true
        stageService.isConfidenceProvided shouldBe true
        stageService.isExplanationProvided shouldBe true
        stageService.isMostImportantVariablesProvided shouldBe false
        stageService.isIterationStarted shouldBe true
        stageService.isIterationFinished shouldBe false
        stageService.isNextIterationAvailable shouldBe true
      }
    }

    describe("when essential variables are provided") {
      val stageService = serviceWithProvidedEssentialVars()

      it("should not allow to do anything but preparing new variables set") {
        intercept[IllegalStateException](stageService.selectVariable(Variable(1, "aaa")))
        intercept[IllegalStateException](stageService.setAnswer(TrialAnswer.Plus))
        intercept[IllegalStateException](stageService.setConfidence(75))
        intercept[IllegalStateException](stageService.setExplanation("explanation"))
        intercept[IllegalStateException](stageService.setEssentialVariables(List(Variable(1, "aaa"))))
      }

      it("should report a valid state") {
        stageService.isAnswerProvided shouldBe true
        stageService.isConfidenceProvided shouldBe true
        stageService.isExplanationProvided shouldBe true
        stageService.isMostImportantVariablesProvided shouldBe true
        stageService.isIterationStarted shouldBe false
        stageService.isIterationFinished shouldBe true
        stageService.isNextIterationAvailable shouldBe true
      }
    }

    describe("when last iteration is finished") {
      val stageService = serviceWithLastIterationFinished()

      it("should not allow to do anything") {
        intercept[IllegalStateException](stageService.prepareVariables())
        intercept[IllegalStateException](stageService.selectVariable(Variable(1, "aaa")))
        intercept[IllegalStateException](stageService.setAnswer(TrialAnswer.Plus))
        intercept[IllegalStateException](stageService.setConfidence(75))
        intercept[IllegalStateException](stageService.setExplanation("explanation"))
        intercept[IllegalStateException](stageService.setEssentialVariables(List(Variable(1, "aaa"))))
      }

      it("should report a valid state") {
        stageService.isAnswerProvided shouldBe true
        stageService.isConfidenceProvided shouldBe true
        stageService.isExplanationProvided shouldBe true
        stageService.isMostImportantVariablesProvided shouldBe true
        stageService.isIterationStarted shouldBe false
        stageService.isIterationFinished shouldBe true
        stageService.isNextIterationAvailable shouldBe false
      }
    }
  }

  describe("TrialStageService validations") {
    describe("when not enough variables are selected") {
      val stageService = serviceWithStartedIteration()
      intercept[IllegalStateException](stageService.setAnswer(TrialAnswer.Plus))
    }

    describe("when one tries to select the same variable twice") {
      val stageService = serviceWithStartedIteration()
      val varDef = stageService.getPreparedVariables().head
      val variable = Variable(varDef.id, varDef.title)
      stageService.selectVariable(variable)
      intercept[IllegalArgumentException](stageService.selectVariable(variable))
    }

    describe("when one tries to select variable which does not belong to initial variables set") {
      val stageService = serviceWithStartedIteration()
      val variable = Variable(532, "adga")
      intercept[IllegalArgumentException](stageService.selectVariable(variable))
    }

    describe("when one tries to select more variables than allowed") {
      val stageService = serviceWithStartedIteration()
      val varDefs = stageService.getPreparedVariables()
      val vars = for (VariableDefinition(_, id, title, _, _) ← varDefs) yield Variable(id, title)
      intercept[IllegalStateException](vars.foreach(stageService.selectVariable))
    }

    describe("when wrong amount of essential variables are provided") {
      val stageService = serviceWithProvidedExplanation()
      val selectedVars = stageService.getSelectedVariables()
      intercept[IllegalArgumentException](stageService.setEssentialVariables(Nil))
      intercept[IllegalArgumentException](stageService.setEssentialVariables(selectedVars))
    }

    describe("when essential variables from outside the previously selected variables are provided") {
      val stageService = serviceWithProvidedExplanation()
      val varDefs = stageService.getPreparedVariables()
      val vars = for (VariableDefinition(_, id, title, _, _) ← varDefs) yield Variable(id, title)
      val selectedVars = stageService.getSelectedVariables()
      val varsToTest = vars.toSet -- selectedVars.toSet
      intercept[IllegalArgumentException](stageService.setEssentialVariables(varsToTest.toList))
    }

    describe("when duplicated essential variables are provided") {
      val stageService = serviceWithProvidedExplanation()
      val selectedVars = Iterator.continually(stageService.getSelectedVariables().head).take(stageService.trialSetup.essentialVarsCount)
      intercept[IllegalArgumentException](stageService.setEssentialVariables(selectedVars.toList))
    }
  }

}
