package net.enigma

import scala.collection.immutable.WrappedString
import scala.language.implicitConversions

import net.enigma.db.TextResourcesDAO

/**
 * @author Jacek Lewandowski
 */
object TextResources {

  sealed trait ResourceGroup

  sealed trait Resource {
    @volatile private[TextResources] var key: String = null

    def isDefined: Boolean = TextResourcesDAO.get(key).isDefined

    def stringValue: String = TextResourcesDAO.get(key).getOrElse(s"??? $key ???")

    def intValue: Int = stringValue.toInt
  }

  implicit def resrouceToWrappedString(resource: Resource): WrappedString = resource.stringValue

  implicit def resrouceToString(resource: Resource): String = resource.stringValue

  object Notifications extends ResourceGroup {

    object MustSelectAtLeastNVariables extends Resource

    object MustSelectAtMostNVariables extends Resource

    object TotalScoreMustBe100 extends Resource

    object LoseTheLottery extends Resource

    object WonTheLottery extends Resource

    object ConfidenceValueInvalid extends Resource

    object RankValueOutOfRange extends Resource

    object LoginFailed extends Resource

    object LoginSuccessful extends Resource

    object NeedToSelectAnswer extends Resource

    object MustSelectExactlyNVariables extends Resource

    object CorrectAnswerProvided extends Resource

    object NoCorrectAnswerProvided extends Resource

    object MustSelectExactlyNEssentialVariables extends Resource

    object MustChooseLotteryOrBet extends Resource

    object ValidationError extends Resource

    object InvalidEmailAddress extends Resource

    object InvalidMissingVariables extends Resource

    object Cookies extends Resource

  }

  object Titles extends ResourceGroup {

    object MostImportantVariables extends Resource

    object Justifications extends Resource

    object ConfidenceQuestion extends Resource

    object Lottery extends Resource

    object Thanks extends Resource

    object Questionnaire extends Resource

    object Instruction extends Resource

    object Trial extends Resource

    object VariablesSelection extends Resource

    object VariablesOrdering extends Resource

    object VariablesScoring extends Resource

    object PersonalData extends Resource

    object Main extends Resource

    object Login extends Resource

    object CannotContinue extends Resource

    object EmailAddress extends Resource

    object MissingVariables extends Resource

    object Admin extends Resource

  }

  object Labels extends ResourceGroup {

    object Lottery extends Resource

    object NotLottery extends Resource

    object ConfirmConfidence extends Resource

    object Confidence extends Resource

    object Score extends Resource

    object Minus extends Resource

    object Plus extends Resource

    object Rank extends Resource

    object Select extends Resource

    object NextButton extends Resource

    object LoginButton extends Resource

    object LoginCodeField extends Resource

    object ExplanationQuestion extends Resource

    object ConfidenceQuestion extends Resource

    object TrialQuestion extends Resource

    object EmailQuestion extends Resource

    object MissingVariables extends Resource

    object AdminSummaryTable extends Resource

    object AdminSummaryTableGroupCol extends Resource

    object AdminSummaryTableNotStartedCol extends Resource

    object AdminSummaryTableNotFinishedCol extends Resource

    object AdminSummaryTableFinishedCol extends Resource

    object AdminGenerateGroupButton extends Resource

    object AdminGenerateUserButton extends Resource

    object AdminGroupName extends Resource

    object AdminGeneratedCodes extends Resource

    object AdminGeneratePanel extends Resource

    object AdminBackup extends Resource


  }

  object Instructions extends ResourceGroup {

    object Lottery extends Resource

    object Thanks extends Resource

    object TrialInstruction extends Resource

    object QuestionnaireInstruction extends Resource

    object InitialInstruction extends Resource

    object VariablesSelection extends Resource

    object VariablesOrdering extends Resource

    object VariablesScoring extends Resource

    object Trial extends Resource

    object ConfidenceQuestion extends Resource

    object Justifications extends Resource

    object MostImportantVariables extends Resource

    object Questionnaire extends Resource

    object LotteryQuestion extends Resource

    object EmailAddress extends Resource

  }

  object Setup extends ResourceGroup {

    object Variables extends ResourceGroup {

      object VariablesCount extends Resource

    }

    object Trial extends ResourceGroup {

      object UnitPrice extends Resource

      object TotalScore extends Resource

      object MaxSelectedVariables extends Resource

      object MinSelectedVariables extends Resource

      object Sequences extends Resource

      object ShuffleRange extends Resource

      object EssentialVariables extends Resource

      object SequenceLength extends Resource

    }

    object Lottery extends ResourceGroup {

      object LastIterationsCount extends Resource

    }

  }

  import scala.reflect.runtime.universe._

  val mirror = runtimeMirror(getClass.getClassLoader)

  private def verify(): Unit = {
    val t = mirror.typeOf[TextResources.type]

    def verify(modules: List[Symbol]): Unit = {
      val name = modules.reverse.map(_.asModule.name).mkString(".")
      val resource = mirror.reflectModule(modules.head.asModule).instance.asInstanceOf[Resource]
      resource.key = name
      if (!resource.isDefined) {
        println(s"INSERT INTO experiment.text_resources (key, value) VALUES ('$name', '');")
      }
    }

    def analyzeModule(modules: List[Symbol]): Unit = {
      for (declaration ← modules.head.asModule.moduleClass.asType.toType.decls if declaration.isModule) {
        val isResourceGroup = declaration.typeSignature.baseClasses.exists(symbol ⇒
          symbol.isType && symbol.asType.toType =:= mirror.typeOf[ResourceGroup])

        val isResource = declaration.typeSignature.baseClasses.exists(symbol ⇒
          symbol.isType && symbol.asType.toType =:= mirror.typeOf[Resource])

        if (isResourceGroup) {
          analyzeModule(declaration :: modules)
        } else if (isResource) {
          verify(declaration :: modules)
        }

      }
    }

    for (declaration ← t.decls if declaration.isModule) {
      analyzeModule(List(declaration))
    }
  }

  verify()

}
