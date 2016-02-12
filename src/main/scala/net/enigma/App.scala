package net.enigma

import java.io.FileOutputStream
import java.nio.file.{Files, Path}
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import java.util.zip.{ZipEntry, ZipOutputStream}

import scala.collection.mutable
import scala.util.Random

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.navigator.{View, ViewProvider}
import com.vaadin.server.VaadinSession
import com.vaadin.ui.UI
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory

import net.enigma.TextResources._
import net.enigma.db.{GroupDAO, StageDataDAO, UserDAO}
import net.enigma.presenter._
import net.enigma.service._
import net.enigma.service.impl._
import net.enigma.views._

/**
 * @author Jacek Lewandowski
 */
object App {
  private val logger = LoggerFactory.getLogger(classOf[App])

  val LINK = "http://badania-inwestorow.pl/trendy-gieldowe/#!login/"
  val ADMIN_USER = "t2734hmtdo2347hdmtso2hs34tog1xo34xfhm3f2z0384gh0234mg0003p3m9xp4hgm93p48nhgz394g"
  val testMode = true

  val providerSuffix = "Provider"
  val userKey = "user"
  val adminKey = "admin"
  val captchaProvidedKey = "captchaProvided"

  private val sessionMap = mutable.WeakHashMap[String, VaadinSession]()

  private val lock = new ReentrantLock()

  private val secureRandom = new SecureRandom()
  private val threadLocalRandom = new ThreadLocal[Random]() {
    override def initialValue(): Random = new Random(secureRandom.synchronized(secureRandom.nextLong()))
  }

  def random = threadLocalRandom.get()

  def ui = UI.getCurrent

  def currentUser: Option[String] = {
    for (ui ← Option(ui);
         session ← Option(ui.getSession);
         user ← Option(ui.getSession.getAttribute(userKey).asInstanceOf[String])) yield user
  }

  def currentUser_=(user: Option[String]) = {
    def closeOtherSession(userCode: String): Unit = {
      sessionMap.get(userCode) match {
        case Some(otherSession) ⇒
          otherSession.lock()
          try {
            otherSession.close()
          } finally {
            otherSession.unlock()
          }
        case None ⇒
      }
    }

    if (lock.tryLock(30, TimeUnit.SECONDS))
      try {
        for (ui ← Option(ui); session ← Option(ui.getSession)) user match {
          case Some(u) ⇒
            closeOtherSession(u)
            sessionMap.put(u, session)
            ui.getSession.setAttribute(userKey, u)
          case None ⇒
            currentUser match {
              case Some(curUser) ⇒
                sessionMap.remove(curUser)
              case None ⇒
            }
            ui.getSession.setAttribute(userKey, null)
        }
      } finally {
        lock.unlock()
      }
  }

  def captchaProvided: Boolean = {
    val r = for (ui <- Option(ui);
                 session <- Option(ui.getSession);
                 captchaProvided <- Option(ui.getSession.getAttribute(captchaProvidedKey).asInstanceOf[Boolean]))
      yield captchaProvided
    r.getOrElse(false)
  }

  def captchaProvided_=(v: Boolean) = {
    for (ui <- Option(ui); session <- Option(ui.getSession))
      session.setAttribute(captchaProvidedKey, v)
  }

  def admin: Boolean = {
    val r = for (ui <- Option(ui);
                 session <- Option(ui.getSession);
                 captchaProvided <- Option(ui.getSession.getAttribute(adminKey).asInstanceOf[Boolean]))
      yield captchaProvided
    r.getOrElse(false)
  }

  def admin_=(v: Boolean) = {
    for (ui <- Option(ui); session <- Option(ui.getSession))
      session.setAttribute(adminKey, v)
  }

  val service = new AppService
    with LoginServiceImpl
    with PersonalDataServiceImpl
    with QuestionnaireServiceImpl
    with ExperimentServiceImpl
    with UserServiceImpl {}

  def loggedIn: Boolean = App.currentUser.isDefined && captchaProvided

  object Views extends ViewProvider {

    private lazy val subProviders = new mutable.HashMap[String, Provider]()

    abstract class Provider(val name: String) {
      subProviders += name → this

      def apply(): View

      def allowed: Boolean
    }

    object Admin extends Provider("admin") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Admin)
          with AdminView with AdminPresenter {

          override def nextView: String = Login.name

          override def allowedToEnter(event: ViewChangeEvent): Boolean = App.testMode || allowed
        }

      override def allowed: Boolean = admin
    }

    object Login extends Provider("login") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Login)
          with LoginView with LoginPresenter {

          override def nextView: String = InitialInstruction.name

          override lazy val allowedToEnter = true
        }

      override def allowed: Boolean = true
    }

    object InitialInstruction extends Provider("welcome") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Instruction)
          with InstructionView with InstructionPresenter {

          override lazy val instructions: String = TextResources.Instructions.InitialInstruction

          override def nextView: String = VariablesSelection.name

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(), forbiddenKeys = Set(VariablesSelection.name))
    }

    object VariablesSelection extends Provider("variables-selection") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.VariablesSelection)
          with VariablesSelectionView with VariablesSelectionPresenter {

          override lazy val stageService: VariablesStageService = App.service.getVariablesStageService

          override def nextView: String = VariablesOrdering.name

          override def instructions: String = TextResources.Instructions.VariablesSelection

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(InitialInstruction.name), forbiddenKeys = Set(VariablesSelection.name))
    }

    object VariablesOrdering extends Provider("variables-ordering") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.VariablesOrdering)
          with VariablesOrderingView with VariablesOrderingPresenter {

          override lazy val stageService: VariablesStageService = App.service.getVariablesStageService

          override def nextView: String = VariablesScoring.name

          override def instructions: String = TextResources.Instructions.VariablesOrdering

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(VariablesSelection.name), forbiddenKeys = Set(VariablesOrdering.name))
    }

    object VariablesScoring extends Provider("variables-scoring") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.VariablesScoring)
          with VariablesScoringView with VariablesScoringPresenter {

          override lazy val stageService: VariablesStageService = App.service.getVariablesStageService

          override def nextView: String = TrialInstruction.name

          override def instructions: String = TextResources.Instructions.VariablesScoring

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(VariablesOrdering.name), forbiddenKeys = Set(VariablesScoring.name))
    }

    object TrialInstruction extends Provider("trial-instruction") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Instruction)
          with InstructionView with InstructionPresenter {

          override lazy val instructions: String = TextResources.Instructions.TrialInstruction

          override def nextView: String = Trial.name

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(VariablesScoring.name), forbiddenKeys = Set(Trial.name))
    }

    object Trial extends Provider("trial") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Trial)
          with TrialView with TrialPresenter {

          override lazy val stageService: TrialStageService = App.service.getTrialStageService

          override def nextView: String = if (allowed) ConfidenceQuestion.name else Justifications.name

          override def instructions: String = TextResources.Instructions.Trial

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(TrialInstruction.name), forbiddenKeys = Set(Trial.name))
    }

    object ConfidenceQuestion extends Provider("confidence-question") {
      override def apply(): View =
        new SimpleView(this.name, TextResources.Titles.ConfidenceQuestion)
          with ConfidenceQuestionView with ConfidenceQuestionPresenter {

          override lazy val stageService: TrialStageService = App.service.getTrialStageService

          override def nextView = MostImportantVariables.name

          override def instructions: String = TextResources.Instructions.ConfidenceQuestion

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(TrialInstruction.name), forbiddenKeys = Set(Trial.name))
    }

    object MostImportantVariables extends Provider("most-important-variables") {
      override def apply(): View =
        new SimpleView(this.name, TextResources.Titles.MostImportantVariables)
          with VariablesSelectionView with MostImportantVariablesPresenter {

          override lazy val stageService: TrialStageService = App.service.getTrialStageService

          override def nextView = Trial.name

          override def instructions: String = TextResources.Instructions.MostImportantVariables

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(TrialInstruction.name), forbiddenKeys = Set(Trial.name))
    }

    object Justifications extends Provider("justifications") {
      override def apply(): View =
        new SimpleView(this.name, TextResources.Titles.Justifications)
          with JustificationsView with JustificationsPresenter {

          override lazy val stageService: JustificationsStageService = App.service.getJustificationsStageService

          override def nextView = Lottery.name

          override def instructions: String = TextResources.Instructions.Justifications

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(Trial.name), forbiddenKeys = Set(Justifications.name))
    }

    object Lottery extends Provider("lottery") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Lottery)
          with LotteryView with LotteryPresenter {

          override lazy val stageService: LotteryStageService = App.service.getLotteryStageService

          override def nextView: String = MissingVariables.name

          override def instructions: String = TextResources.Instructions.Lottery.format(s"${stageService.getLotteryWinChance}%")

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(Justifications.name), forbiddenKeys = Set(Lottery.name))
    }

    object MissingVariables extends Provider("missing-variables") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.MissingVariables)
          with MissingVariablesQuestionView with MissingVariablesQuestionPresenter {

          override def missingVariablesStageService: MissingVariablesStageService = App.service.getMissingVariablesStageService

          override def nextView: String = PersonalData.name

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(Lottery.name), forbiddenKeys = Set(MissingVariables.name))
    }

    //    object QuestionnaireInstruction extends Provider("questionnaire-instruction") {
    //      override def apply() =
    //        new SimpleView(this.name, TextResources.Titles.Instruction)
    //            with InstructionView with InstructionPresenter {
    //
    //          override lazy val instructions: String = TextResources.Instructions.QuestionnaireInstruction
    //
    //          override def nextView: String = Questionnaire.name
    //
    //          override lazy val allowedToEnter = App.testMode ||
    //              loggedIn && App.service.checkForCompletedStages(
    //                requiredKeys = Set(Lottery.name), forbiddenKeys = Set())
    //        }
    //    }
    //
    //    object Questionnaire extends Provider("questionnaire") {
    //      override def apply() =
    //        new SimpleView(this.name, TextResources.Titles.Questionnaire)
    //            with SurveyView with QuestionnairePresenter {
    //
    //          override def nextView: String = PersonalData.name
    //
    //          override def instructions: String = TextResources.Instructions.Questionnaire
    //
    //          override lazy val allowedToEnter = App.testMode ||
    //              loggedIn && App.service.checkForCompletedStages(
    //                requiredKeys = Set(QuestionnaireInstruction.name), forbiddenKeys = Set(Questionnaire.name))
    //        }
    //    }

    object PersonalData extends Provider("personal-data") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.PersonalData)
          with SurveyView with PersonalDataPresenter {

          override def nextView: String = EmailAddress.name

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(Lottery.name), forbiddenKeys = Set(PersonalData.name))
    }

    object EmailAddress extends Provider("email-address") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.EmailAddress)
          with OpenQuestionView with EmailPresenter {

          override def nextView: String = Thanks.name

          override def instructions: String = TextResources.Instructions.EmailAddress

          override def allowedToEnter = App.testMode || allowed

          override def userService: UserService = App.service
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(PersonalData.name), forbiddenKeys = Set(EmailAddress.name))
    }

    object Thanks extends Provider("thanks") {
      override def apply() =
        new SimpleView(this.name, TextResources.Titles.Thanks)
          with InstructionView with InstructionPresenter {

          override lazy val instructions: String = TextResources.Instructions.Thanks

          override def nextView: String = Login.name

          override def allowedToEnter = App.testMode || allowed
        }

      override def allowed = loggedIn && App.service.checkForCompletedStages(
        requiredKeys = Set(EmailAddress.name), forbiddenKeys = Set())
    }

    override def getViewName(viewAndParameters: String): String = {
      logger.info(s"Getting view name for $viewAndParameters")
      subProviders.keys
        .find(key ⇒ viewAndParameters == key || viewAndParameters.startsWith(s"$key/"))
        .map(foundView ⇒ {
        logger.info(s"Going to view $foundView with params $viewAndParameters")
        foundView
      })
        .getOrElse(findAllowedProvider())
    }

    override def getView(viewName: String): View = {
      logger.info(s"Getting view for name $viewName")
      subProviders.get(viewName).find(_.allowed).getOrElse(subProviders(findAllowedProvider())).apply()
    }

    import scala.reflect.runtime.universe._

    val mirror = runtimeMirror(getClass.getClassLoader)

    private def initialize(): Unit = {
      val t = mirror.typeOf[Views.type]

      def touch(modules: List[Symbol]): Unit = {
        val name = modules.map(_.asModule.name).mkString(".")
        val resource = mirror.reflectModule(modules.head.asModule).instance.asInstanceOf[Resource]
        if (!resource.isDefined) {
          println(s"INSERT INTO experiment.text_resources (key, value) VALUES ('$name', '');")
        }
      }

      def touchModule(module: ModuleSymbol): Unit = {
        val resource = mirror.reflectModule(module).instance.asInstanceOf[Provider]
        logger.info(s"Registering view ${resource.name}")
      }

      for (declaration ← t.decls
           if declaration.isModule
           if declaration.typeSignature.baseClasses.exists(symbol ⇒
             symbol.isType && symbol.asType.toType =:= mirror.typeOf[Provider])) {
        touchModule(declaration.asModule)
      }
    }

    initialize()

    def findAllowedProvider() = {
      if (currentUser.isEmpty) {
        Login.name
      } else {
        val availableProviders = subProviders - Login.name
        val selectedProviders = for (provider ← availableProviders.values if provider.allowed)
          yield provider.name

        logger.info(s"Found $selectedProviders providers")
        selectedProviders.headOption.getOrElse(Login.name)
      }
    }
  }

  def backup(): Path = {
    val sdf = new SimpleDateFormat("YYYYMMDDHHmmss")
    val path = Files.createTempFile(sdf.format(new Date()), "backup")
    val fout = new FileOutputStream(path.toFile)
    try {
      val zout = new ZipOutputStream(fout)
      zout.putNextEntry(new ZipEntry("users.json"))
      UserDAO.backup(zout)
      zout.closeEntry()

      zout.putNextEntry(new ZipEntry("groups.json"))
      GroupDAO.backup(zout)
      zout.closeEntry()

      zout.putNextEntry(new ZipEntry("stages.json"))
      StageDataDAO.backup(zout)
      zout.closeEntry()

      zout.close()
    } finally {
      IOUtils.closeQuietly(fout)
    }
    path
  }

}
