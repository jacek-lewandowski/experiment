package net.enigma

import java.security.SecureRandom

import scala.collection.mutable
import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.util.Random

import com.vaadin.navigator.{View, ViewProvider}
import com.vaadin.ui.UI
import org.slf4j.LoggerFactory

import net.enigma.model.User
import net.enigma.presenter._
import net.enigma.service._
import net.enigma.service.impl._
import net.enigma.views._

/**
 * @author Jacek Lewandowski
 */
object App {
  private val logger = LoggerFactory.getLogger(classOf[App])

  val testMode = true

  val providerSuffix = "Provider"
  val userKey = "user"

  private val secureRandom = new SecureRandom()
  private val threadLocalRandom = new ThreadLocal[Random]() {
    override def initialValue(): Random = new Random(secureRandom.synchronized(secureRandom.nextLong()))
  }
  def random = threadLocalRandom.get()

  def ui = UI.getCurrent

  def currentUser: Option[User] =
    for (ui ← Option(ui);
         session ← Option(ui.getSession);
         user ← Option(ui.getSession.getAttribute(userKey).asInstanceOf[User])) yield user

  def currentUser_=(user: Option[User]) =
    for (ui ← Option(ui); session ← Option(ui.getSession)) user match {
      case Some(u) ⇒
        ui.getSession.setAttribute(userKey, u)
      case None ⇒
        ui.getSession.setAttribute(userKey, null)
    }

  val service = new AppService
      with LoginServiceImpl
      with PersonalDataServiceImpl
      with QuestionnaireServiceImpl
      with ExperimentServiceImpl
      with UserServiceImpl {}

  object Views extends ViewProvider {

    private lazy val subProviders = new mutable.HashMap[String, Provider]()

    abstract class Provider(val name: String) {
      subProviders += name → this

      def apply(): View
    }

    object Login extends Provider("login") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Login)
            with LoginView with LoginPresenter {

          override def nextView: String = InitialInstruction.name

          override lazy val allowedToEnter = true
        }
    }

    object InitialInstruction extends Provider("welcome") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Instruction)
            with InstructionView with InstructionPresenter {

          override lazy val instructions: String = TextResources.Instructions.InitialInstruction

          override def nextView: String = VariablesSelection.name

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(), forbiddenKeys = Set())
        }
    }

    object VariablesSelection extends Provider("variables-selection") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.VariablesSelection)
            with VariablesSelectionView with VariablesSelectionPresenter {

          override lazy val stageService: VariablesStageService = App.service.getVariablesStageService

          override def nextView: String = VariablesOrdering.name

          override def instructions: String = TextResources.Instructions.VariablesSelection

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(InitialInstruction.name), forbiddenKeys = Set(VariablesSelection.name))
        }
    }

    object VariablesOrdering extends Provider("variables-ordering") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.VariablesOrdering)
            with VariablesOrderingView with VariablesOrderingPresenter {

          override lazy val stageService: VariablesStageService = App.service.getVariablesStageService

          override def nextView: String = VariablesScoring.name

          override def instructions: String = TextResources.Instructions.VariablesOrdering

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(VariablesSelection.name), forbiddenKeys = Set(VariablesOrdering.name))
        }
    }

    object VariablesScoring extends Provider("variables-scoring") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.VariablesScoring)
            with VariablesScoringView with VariablesScoringPresenter {

          override lazy val stageService: VariablesStageService = App.service.getVariablesStageService

          override def nextView: String = TrialInstruction.name

          override def instructions: String = TextResources.Instructions.VariablesScoring

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(VariablesOrdering.name), forbiddenKeys = Set(VariablesScoring.name))
        }
    }

    object TrialInstruction extends Provider("trial-instruction") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Instruction)
            with InstructionView with InstructionPresenter {

          override lazy val instructions: String = TextResources.Instructions.TrialInstruction

          override def nextView: String = Trial.name

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(VariablesScoring.name), forbiddenKeys = Set())
        }
    }

    object Trial extends Provider("trial") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Trial)
            with TrialView with TrialPresenter {

          override lazy val stageService: TrialStageService = App.service.getTrialStageService

          override def nextView: String = ConfidenceQuestion.name

          override def instructions: String = TextResources.Instructions.Trial

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(TrialInstruction.name), forbiddenKeys = Set(Trial.name))
        }
    }

    object ConfidenceQuestion extends Provider("confidence-question") {
      override def apply(): View =
        new SimpleView(this.name, TextResources.Titles.ConfidenceQuestion)
            with OpenQuestionView with ConfidenceQuestionPresenter {

          override lazy val stageService: TrialStageService = App.service.getTrialStageService

          override def nextView = ExplanationQuestion.name

          override def instructions: String = TextResources.Instructions.ConfidenceQuestion

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(TrialInstruction.name), forbiddenKeys = Set(Trial.name)) // TODO check iteration state
        }
    }

    object ExplanationQuestion extends Provider("explanation-question") {
      override def apply(): View =
        new SimpleView(this.name, TextResources.Titles.ExplanationQuestion)
            with OpenQuestionView with ExplanationPresenter {

          override lazy val stageService: TrialStageService = App.service.getTrialStageService

          override def nextView = MostImportantVariables.name

          override def instructions: String = TextResources.Instructions.ExplanationQuestion

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(TrialInstruction.name), forbiddenKeys = Set(Trial.name)) // TODO check iteration state
        }
    }

    object MostImportantVariables extends Provider("most-important-variables") {
      override def apply(): View =
        new SimpleView(this.name, TextResources.Titles.MostImportantVariables)
            with VariablesSelectionView with MostImportantVariablesPresenter {

          override lazy val stageService: TrialStageService = App.service.getTrialStageService

          override def nextView = if (stageService.isNextIterationAvailable) Trial.name else Lottery.name

          override def instructions: String = TextResources.Instructions.MostImportantVariables

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(TrialInstruction.name), forbiddenKeys = Set(Trial.name)) // TODO check iteration state
        }
    }

    object Lottery extends Provider("lottery") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Lottery)
            with LotteryView with LotteryPresenter {

          override lazy val stageService: LotteryStageService = App.service.getLotteryStageService

          override def nextView: String = QuestionnaireInstruction.name

          override def instructions: String = TextResources.Instructions.Lottery.format(s"${stageService.getLotteryWinChance}%")

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(Trial.name), forbiddenKeys = Set(Lottery.name))
        }
    }

    object QuestionnaireInstruction extends Provider("questionnaire-instruction") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Instruction)
            with InstructionView with InstructionPresenter {

          override lazy val instructions: String = TextResources.Instructions.QuestionnaireInstruction

          override def nextView: String = Questionnaire.name

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(Lottery.name), forbiddenKeys = Set())
        }
    }

    object Questionnaire extends Provider("questionnaire") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Questionnaire)
            with SurveyView with QuestionnairePresenter {

          override def nextView: String = PersonalData.name

          override def instructions: String = TextResources.Instructions.Questionnaire

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(QuestionnaireInstruction.name), forbiddenKeys = Set(Questionnaire.name))
        }
    }

    object PersonalData extends Provider("personal-data") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.PersonalData)
            with SurveyView with PersonalDataPresenter {

          override def nextView: String = Thanks.name

          override def instructions: String = TextResources.Instructions.PersonalData

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(Questionnaire.name), forbiddenKeys = Set(PersonalData.name))
        }
    }

    object Thanks extends Provider("thanks") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Thanks)
            with InstructionView with InstructionPresenter {

          override lazy val instructions: String = TextResources.Instructions.Thanks

          override def nextView: String = Login.name

          override lazy val allowedToEnter = App.testMode ||
              App.currentUser.isDefined && App.service.checkForCompletedStages(
                requiredKeys = Set(PersonalData.name), forbiddenKeys = Set())
        }
    }

    override def getViewName(viewAndParameters: String): String = {
      logger.info(s"Getting view name for $viewAndParameters")
      subProviders.keys
          .find(key ⇒ viewAndParameters == key || viewAndParameters.startsWith(s"$key/"))
          .getOrElse(Login.name)
    }

    override def getView(viewName: String): View = {
      logger.info(s"Getting view for name $viewName")
      subProviders.get(viewName).map(_.apply()).getOrElse(Login())
    }
  }

}
