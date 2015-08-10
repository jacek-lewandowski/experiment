package net.enigma

import java.io.{OutputStream, PrintStream}
import java.nio.charset.Charset
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import org.apache.commons.lang3.StringUtils
import org.json4s.native.JsonParser._

import scala.collection.JavaConversions._

class DataReader {

}

object DataReader {

  case class UserResults(
    userCode: String,
    category: String,
    emailAddress: Option[String])

  case class PersonalData(
    gender: String,
    age: Int,
    investingExperience: Int,
    investingTime: String,
    education: String)

  case class VariablesData(
    orderingSequence: String,
    scoringSequence: String,
    distance: Int,
    weights: Map[String, Int],
    timestamp: Long)

  case class LotteryData(
    result: Boolean,
    lottery: Boolean,
    winChance: Int,
    timestamp: Long)

  case class IterationData(
    idx: Int,
    selectedAnswer: String,
    confidence: Int,
    sequence: String,
    initVarsSeq: String,
    selectedVarsSeq: String,
    selectedVarsValuesSeq: String,
    essentialVarsSet: Set[String],
    selectedVars: Seq[(String, String, Long)])

  case class TrialData(
    timestamp: Long,
    iterations: IndexedSeq[IterationData])

  case class Result(
    user: UserResults,
    personal: PersonalData,
    variables: VariablesData,
    lottery: LotteryData,
    trial: TrialData)

  val allVars = 'A' to 'P'
  def columns(allEmails: Map[String, Seq[(String, String)]]) = Vector(
    CSVColumn("user.code", _.user.userCode),
    CSVColumn("user.category", _.user.category),
    CSVColumn("user.email", _.user.emailAddress.getOrElse("")),
    CSVColumn("user.email.provided", _.user.emailAddress.fold("0")(_ => "1")),
    CSVColumn("user.email.duplicate", x => x.user.emailAddress.flatMap(email => allEmails.get(email)).fold("0")(y => if (y.size > 1) "1" else "0")),
    CSVColumn("user.gender", _.personal.gender),
    CSVColumn("user.age", _.personal.age.toString),
    CSVColumn("user.investingExperience", _.personal.investingExperience.toString),
    CSVColumn("user.investingTime", _.personal.investingTime),
    CSVColumn("user.education", _.personal.education),
    CSVColumn("lottery.lottery", _.lottery.lottery.toString),
    CSVColumn("lottery.result", _.lottery.result.toString),
    CSVColumn("lottery.winChance", _.lottery.winChance.toString),
    CSVColumn("lottery.timestamp", _.lottery.timestamp.toString),
    CSVColumn("variables.startTime", _.variables.timestamp.toString),
    CSVColumn("variables.orderingSeq", _.variables.orderingSequence),
    CSVColumn("variables.scoringSeq", _.variables.scoringSequence),
    CSVColumn("variables.orderingToScoringDist", _.variables.distance.toString)
  ) ++ allVars.map(v => CSVColumn(s"variables.weight.$v", _.variables.weights.getOrElse(v.toString, "").toString)) ++
  Vector(
    CSVColumn("trial.startTime", _.trial.timestamp.toString)
  ) ++ (0 until 10).flatMap{idx =>
    val prefix = s"trial.iterations.$idx"

    def distToOrdered(idx: Int)(row: Result) =
      StringUtils.getLevenshteinDistance(row.variables.orderingSequence, row.trial.iterations(idx).selectedVarsSeq).toString
    def distToScored(idx: Int)(row: Result) =
      StringUtils.getLevenshteinDistance(row.variables.scoringSequence, row.trial.iterations(idx).selectedVarsSeq).toString
    def distToInitial(idx: Int)(row: Result) =
      StringUtils.getLevenshteinDistance(row.trial.iterations(idx).initVarsSeq, row.trial.iterations(idx).selectedVarsSeq).toString
    def selectedIdx(idx:Int, v:String)(row: Result) = {
      val it = row.trial.iterations(idx)
      it.selectedVars.find(_._1 == v).map(elem => it.selectedVars.indexOf(elem)).getOrElse("").toString
    }
    def isEssential(idx:Int, v:String)(row: Result) = {
      val it = row.trial.iterations(idx)
      if (it.essentialVarsSet.contains(v)) "1" else ""
    }

    Vector(
      CSVColumn(s"$prefix.idx", _.trial.iterations(idx).idx.toString),
      CSVColumn(s"$prefix.initVarsSeq", _.trial.iterations(idx).initVarsSeq),
      CSVColumn(s"$prefix.selectedVarsSeq", _.trial.iterations(idx).selectedVarsSeq),
      CSVColumn(s"$prefix.distToOrdered", distToOrdered(idx)),
      CSVColumn(s"$prefix.distToScored", distToScored(idx)),
      CSVColumn(s"$prefix.distToInitial", distToInitial(idx)),
      CSVColumn(s"$prefix.answer", _.trial.iterations(idx).selectedAnswer),
      CSVColumn(s"$prefix.confidence", _.trial.iterations(idx).confidence.toString),
      CSVColumn(s"$prefix.sequence", _.trial.iterations(idx).sequence),
      CSVColumn(s"$prefix.realSequenceLength", _.trial.iterations(idx).selectedVars.length.toString),
      CSVColumn(s"$prefix.realSequence", _.trial.iterations(idx).selectedVarsValuesSeq)) ++
      allVars.map(v => CSVColumn(s"$prefix.vars.$v.selIdx", selectedIdx(idx, v.toString))) ++
      allVars.map(v => CSVColumn(s"$prefix.vars.$v.essential", isEssential(idx, v.toString)))
  }

  val UTF8 = Charset.forName("utf-8")

  val usersFile = Paths.get("data/users.json")
  val groupsFile = Paths.get("data/groups.json")
  val stagesFile = Paths.get("data/stages.json")

  val categories = Set(
    "blogi",
    "Stowarzyszenie II",
    "BDM",
    "sandra",
    "Sandra-facebook",
    "M.Bijak-kontakty",
    "mmigacz")

  def encodeVar(v: Any): String = ('A' + v.toString.toInt - 1).toChar.toString

  def getPersonalData(row: (Any, Map[Any, Map[(Any, Option[Int]), Map[String, _]]])): Option[PersonalData] = {
    row._2.get("personalData").map { personalDataStage =>
      val answers = personalDataStage
        .values.map(_.apply("data"))
        .map(_.asInstanceOf[Map[String, Any]])
        .groupBy(_.apply("questionId").toString.toInt)
        .mapValues(_.head.apply("answer").toString)

      PersonalData(answers(1), answers(2).toInt, answers(3).toInt, answers(5), answers(4))
    }
  }

  def getVariables(row: (Any, Map[Any, Map[(Any, Option[Int]), Map[String, _]]])): Option[VariablesData] = {
    row._2.get("variables").map { variablesStage =>
      val variables = variablesStage.values.head.get("data")
        .map(_.asInstanceOf[Map[String, Any]]).get
        .get("variables")
        .map(_.asInstanceOf[List[Map[String, Any]]])
        .get.map(v => (encodeVar(v("id")), v("ordinalNumber").toString.toInt, v("score").toString.toInt))

      val orderingSequence = variables.sortBy(x => (x._2, x._1)).map(_._1).mkString
      val scoringSequence = variables.sortBy(x => (x._3, x._1)).reverse.map(_._1).mkString
      val variablesWeights = variables.map(v => (v._1, v._3)).toMap
      val distance = StringUtils.getLevenshteinDistance(orderingSequence, scoringSequence)
      val timestamp = variablesStage.values.head.get("data")
        .map(_.asInstanceOf[Map[String, Any]]).get
        .apply("timestamp").toString.toLong

      VariablesData(orderingSequence, scoringSequence, distance, variablesWeights, timestamp)
    }
  }

  def getLottery(row: (Any, Map[Any, Map[(Any, Option[Int]), Map[String, _]]])): Option[LotteryData] = {
    row._2.get("lottery").map { lotteryStageData =>
      val lottery = lotteryStageData.values.head.get("data").map(_.asInstanceOf[Map[String, Any]]).get

      LotteryData(
        lottery("result").toString.toBoolean,
        lottery("lotterySelected").toString.toBoolean,
        lottery("winChance").toString.toInt,
        lottery("timestamp").toString.toLong)
    }
  }

  def getTrial(row: (Any, Map[Any, Map[(Any, Option[Int]), Map[String, _]]])): Option[TrialData] = {
    row._2.get("trial").map { trialStageData =>

      val stageInfo = trialStageData(("stageInfo", Some(0))).apply("data").asInstanceOf[Map[String, Any]]
      val timestamp = stageInfo("timestamp").toString.toLong

      val iterations = trialStageData.filterKeys(_._1 == "iteration").toIndexedSeq
        .sortBy(_._1._2.get)
        .map(_._2.asInstanceOf[Map[String, Any]].apply("data").asInstanceOf[Map[String, Any]])

      val iterationsData = iterations.zipWithIndex.map{case (iteration, idx) =>
        val selectedAnswer = iteration("selectedAnswer").toString
        val confidence = iteration("confidence").toString.toInt
        val sequence = iteration("sequence").asInstanceOf[List[Any]].map(x => if (x == "Plus") "P" else "M").mkString
        val initVarsSeq = iteration("initVars").asInstanceOf[List[Map[String, Any]]].map(v => encodeVar(v("id"))).mkString
        val selectedVars = iteration("selectedVars").asInstanceOf[List[Map[String, Any]]].map(v =>
          (encodeVar(v("variable").asInstanceOf[Map[String, Any]].apply("id")), v("value").toString, v("timestamp").toString.toLong))
        val selectedVarsSeq = selectedVars.map(_._1).mkString
        val selectedVarsValuesSeq = selectedVars.map(x => if (x._2 == "Plus") "P" else "M").mkString
        val essentialVarsSet = iteration("essentialVars").asInstanceOf[List[Map[String, Any]]].map(v => encodeVar(v("id"))).toSet

        IterationData(idx, selectedAnswer, confidence, sequence, initVarsSeq, selectedVarsSeq, selectedVarsValuesSeq, essentialVarsSet, selectedVars)
      }

      TrialData(timestamp, iterationsData.sortBy(it => (it.sequence, it.selectedVarsValuesSeq)))
    }
  }

  def main(args: Array[String]) {
    val usersRaw = Files.readAllLines(usersFile, UTF8)
    val groupsRaw = Files.readAllLines(groupsFile, UTF8)
    val stagesRaw = Files.readAllLines(stagesFile, UTF8)

    val usersData = usersRaw.map(parse).map(_.values.asInstanceOf[Map[String, _]])
      .filter(_.get("currentStage").contains("thanks"))
      .map(row => UserResults(
        row.get("code").get.toString,
        row.get("category").get.toString,
        row.get("email").find(_.toString.trim != "").map(_.toString)))
      .filter(u => categories.contains(u.category))

    val stagesData = stagesRaw.map(parse).map(_.values.asInstanceOf[Map[String, _]])
      .groupBy(_.apply("user_code"))
      .mapValues(_.groupBy(_.apply("stage")).mapValues(_.groupBy(x => (x.apply("key"), x.get("idx").map(_.toString.toInt))).mapValues(_.head)))
      .flatMap(row => (for (
        personalData <- getPersonalData(row);
        variables <- getVariables(row);
        lottery <- getLottery(row);
        trial <- getTrial(row)) yield (personalData, variables, lottery, trial)).map(d => (row._1.toString, d)))

    val results = usersData.flatMap(u => stagesData.get(u.userCode).map(d => Result(u, d._1, d._2, d._3, d._4)))
    val allEmails = for (result <- results; email <- result.user.emailAddress) yield (email, result.user.userCode)

    val cols = columns(allEmails.groupBy(_._1))
    saveResults(results, cols, Paths.get("data/results.csv"))
    println(s"Saved ${results.size} results / ${cols.size}")
  }

  case class CSVColumn(name: String, valueExtractor: Result => String)

  def makeCSV(data: Iterable[Result], columns: IndexedSeq[CSVColumn], out: OutputStream): Unit = {
    val ps = new PrintStream(out, true, "utf-8")
    ps.println(columns.map(_.name).map(s => "\"" + s + "\"").mkString(","))
    for (row <- data) {
      ps.println(columns.map(c => c.valueExtractor(row)).map(s => "\"" + s + "\"").mkString(","))
    }
  }

  def saveResults(data: Iterable[Result], columns: IndexedSeq[CSVColumn], path: Path): Unit = {
    val out = Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
    try {
      makeCSV(data, columns, out)
    } catch {
      case e: Exception => e.printStackTrace()
    }
    out.close()
  }
}
