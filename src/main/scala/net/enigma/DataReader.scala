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
    CSVColumn("user.experience", _.personal.investingExperience.toString),
    CSVColumn("user.investingTime", _.personal.investingTime),
    CSVColumn("user.education", _.personal.education),
    CSVColumn("Czy_loteria", r => if (r.lottery.lottery) "loteria" else "zaklad", title = "Czy wybrana zostalą loteria czy zaklad", quoted = false),
    CSVColumn("Wynik", _.lottery.result.toString, title = "Wynik loterii lub zakladu", quoted = false),
    CSVColumn("Szansa_wygr", _.lottery.winChance.toString, title = "Szansa wygranej / średnia pewność z 2 ost, prob", quoted = false),
    CSVColumn("lottery.timestamp", _.lottery.timestamp.toString),
    CSVColumn("v.startTime", _.variables.timestamp.toString),
    CSVColumn("v.ordSeq", _.variables.orderingSequence),
    CSVColumn("v.scorSeq", _.variables.scoringSequence),
    CSVColumn("v.ordToScorDist", _.variables.distance.toString)
  ) ++
    allVars.map(v => CSVColumn(s"Waga_${varToString(v)}", _.variables.weights.getOrElse(v.toString, "").toString, title = s"Waga ${varToString(v)}", quoted = false)) ++
    Vector(
      CSVColumn("t.startTime", _.trial.timestamp.toString)
    ) ++ allVars.map(v => CSVColumn(s"Liczba_wyb_${decodeVar(v)}", selectionCount(v), title = s"Liczba wyb. ${varToString(v)} we wszystkich probach", quoted = false)) ++
    Seq(CSVColumn("Liczba_wsk", _.trial.iterations.map(_.selectedVarsSeq.length).sum.toString, title = "Calkowita liczba wyb. wsk.", quoted = false)) ++
    //    (0 until 10).flatMap { idx =>
    //      val prefix = s"t.it.$idx"
    //
    //      def distToOrdered(idx: Int)(row: Result) =
    //        StringUtils.getLevenshteinDistance(row.variables.orderingSequence, row.trial.iterations(idx).selectedVarsSeq).toString
    //      def distToScored(idx: Int)(row: Result) =
    //        StringUtils.getLevenshteinDistance(row.variables.scoringSequence, row.trial.iterations(idx).selectedVarsSeq).toString
    //      def distToInitial(idx: Int)(row: Result) =
    //        StringUtils.getLevenshteinDistance(row.trial.iterations(idx).initVarsSeq, row.trial.iterations(idx).selectedVarsSeq).toString
    //      def selectedIdx(idx: Int, v: String)(row: Result) = {
    //        val it = row.trial.iterations(idx)
    //        it.selectedVars.find(_._1 == v).map(elem => it.selectedVars.indexOf(elem)).getOrElse("").toString
    //      }
    //
    //      Vector(
    //        CSVColumn(s"$prefix.initSeq", _.trial.iterations(idx).initVarsSeq),
    //        CSVColumn(s"$prefix.selSeq", _.trial.iterations(idx).selectedVarsSeq),
    //        CSVColumn(s"$prefix.distToOrd", distToOrdered(idx)),
    //        CSVColumn(s"$prefix.distToScor", distToScored(idx)),
    //        CSVColumn(s"$prefix.distToInit", distToInitial(idx)),
    //        CSVColumn(s"$prefix.seq", _.trial.iterations(idx).sequence),
    //        CSVColumn(s"$prefix.realSeq", _.trial.iterations(idx).selectedVarsValuesSeq))
    //    } ++
    (1 to 7).map(pos => CSVColumn(s"Ranga$pos",
      row => decodeVar(row.variables.orderingSequence.charAt(pos - 1)),
      title = s"R, wsk. wyb. z rangą $pos",
      quoted = false)) ++
    (for (iteration <- 1 to 10; selection <- 1 to 10) yield {
      CSVColumn(s"Odkr${selection}_p${iteration}",
        _.trial.iterations(iteration - 1).selectedVarsSeq.toCharArray.lift(selection - 1).map(decodeVar).getOrElse(""),
        title = s"P$iteration, wsk. w odkr. $selection",
        quoted = false)
    }) ++
    allVars.map(v => CSVColumn(s"Wskaznik${decodeVar(v)}",
      idxOfVar(v),
      title = s"R, ranga z jaką byl wyb. ${varToString(v)}",
      quoted = false)) ++
    (for (iteration <- 1 to 10; v <- allVars) yield {
      CSVColumn(s"Wskaznik${decodeVar(v)}_p$iteration", idxOfVar(iteration - 1, v.toString),
        title = s"P$iteration, odkr. w ktorym byl wyb. ${varToString(v)}",
        quoted = false)
    }) ++
    (for (iteration <- 1 to 10; v <- allVars) yield {
      CSVColumn(s"MP${decodeVar(v)}_p$iteration", isEssential(iteration - 1, v.toString),
        title = s"P$iteration, czy ${varToString(v)} byl wyb. jako najwazniejszy",
        quoted = false)
    }) ++
    (for (v <- allVars) yield {
      CSVColumn(s"MP${decodeVar(v)}", row => (0 until 10).count(it => row.trial.iterations(it).essentialVarsSet.contains(v.toString)).toString,
        title = s"Ile razy ${varToString(v)} byl wyb. jako najwazniejszy",
        quoted = false)
    }) ++
    (1 to 10).map { iteration =>
      CSVColumn(s"Pozycja_p$iteration", row => (row.trial.iterations(iteration - 1).idx + 1).toString, title = s"P$iteration, nr porządkowy", quoted = false)
    } ++
    (1 to 10).map { iteration =>
      CSVColumn(s"Odpowiedz_p$iteration", row => convertAnswer(row.trial.iterations(iteration - 1).selectedAnswer), title = s"P$iteration, odpowiedz")
    } ++
    (1 to 10).map { iteration =>
      CSVColumn(s"Pewność_p$iteration", _.trial.iterations(iteration - 1).confidence.toString, title = s"P$iteration, pewność", quoted = false)
    } ++
    (1 to 10).map { iteration =>
      CSVColumn(s"Liczba_wsk_p$iteration", _.trial.iterations(iteration - 1).selectedVars.length.toString, title = s"P$iteration, liczba wskaznikow", quoted = false)
    } ++
    (for (itGroup <- Seq((0 to 9, "all"), (0 to 1, "12"), (2 to 7, "38"), (8 to 9, "910"));
          answer <- Seq("Plus", "Minus");
          v <- allVars)
      yield {
        CSVColumn(s"MP${decodeVar(v)}_${itGroup._2}_${convertAnswer(answer)}",
          row => row.countEssentialsRatio(itGroup._1, v, answer).toString,
          quoted = false, enabled = true)
    })


  implicit class RichRow(val row: DataReader.Result) extends AnyVal {
    def countEssentials(iterations: Seq[Int], variable: Char, ifAnswerIs: String): Int = {
      iterations.map(row.trial.iterations.apply)
        .filter(_.selectedAnswer == ifAnswerIs)
        .count(_.essentialVarsSet.contains(variable.toString))
    }

    def countAnswers(iterations: Seq[Int], ifAnswerIs: String): Int = {
      iterations.map(row.trial.iterations.apply).count(_.selectedAnswer == ifAnswerIs)
    }

    def countEssentialsRatio(iterations: Seq[Int], variable: Char, ifAnswerIs: String): Double = {
      val cntAnswers = countAnswers(iterations, ifAnswerIs)
      if (cntAnswers == 0)
        0d else countEssentials(iterations, variable, ifAnswerIs).toDouble / cntAnswers.toDouble
    }
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

  val varToString = Map(
    ('A', "C/Z"),
    ('B', "Wstega Boll."),
    ('C', "WIG20; WIG80"),
    ('D', "Formacje"),
    ('E', "Obroty giel."),
    ('F', "NASDAQ, DAX"),
    ('G', "Ceny mieszkan"),
    ('H', "Ropa"),
    ('I', "Zloto"),
    ('J', "Bezrobocie"),
    ('K', "Sprzedaz det."),
    ('L', "Handel; Saldo"),
    ('M', "Stopy ref."),
    ('N', "Wskaznik kon."),
    ('O', "PKB w PL"),
    ('P', "Deficyt bud.")
  )

  def encodeVar(v: Any): String = ('A' + v.toString.toInt - 1).toChar.toString

  def decodeVar(v: Char): String = ((v.toInt - 'A'.toInt) + 1).toString

  def isEssential(iteration: Int, v: String)(row: Result) = {
    val it = row.trial.iterations(iteration)
    if (it.essentialVarsSet.contains(v)) "1" else "0"
  }

  def idxOfVar(v: Char)(row: Result) = {
    val idx = row.variables.orderingSequence.indexOf(v)
    if (idx >= 0) (idx + 1).toString else ""
  }

  def idxOfVar(iteration: Int, v: String)(row: Result) = {
    val it = row.trial.iterations(iteration)
    it.selectedVars.find(_._1 == v).map(elem => it.selectedVars.indexOf(elem) + 1).getOrElse("").toString
  }

  def selectionCount(v: Char)(row: Result) =
    row.trial.iterations.count(_.selectedVarsSeq.contains(v)).toString

  def convertAnswer(s: String) = if (s.toLowerCase == "plus") "hossa" else "bessa"

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

      val iterationsData = iterations.zipWithIndex.map { case (iteration, idx) =>
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

      TrialData(timestamp, iterationsData.sortBy(it => (it.sequence, it.idx)))
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
    val duplicatedEmails = allEmails.groupBy(_._1).filter(_._2.size > 1).keySet
    println(duplicatedEmails)

    val cols = columns(allEmails.groupBy(_._1))
    saveResults(results.filterNot(r => r.user.emailAddress.fold(false)(duplicatedEmails.contains)), cols, Paths.get("data/results.csv"))
    saveColumns(cols, Paths.get("data/columns.csv"))
    println(s"Saved ${results.filterNot(r => r.user.emailAddress.fold(false)(duplicatedEmails.contains)).size} results / ${cols.size}")
  }

  case class CSVColumn(name: String, valueExtractor: Result => String, title: String = "", quoted: Boolean = true, enabled: Boolean = true)

  def makeCSV(data: Iterable[Result], columns: IndexedSeq[CSVColumn], out: OutputStream): Unit = {
    val ps = new PrintStream(out, true, "utf-8")
    ps.println(columns.map(_.name).map(s => "\"" + s + "\"").mkString(","))
    for (row <- data) {
      ps.println(columns.filter(_.enabled).map(c => if (c.quoted) "\"" + c.valueExtractor(row) + "\"" else c.valueExtractor(row)).mkString(","))
    }
  }

  def saveResults(data: Iterable[Result], columns: IndexedSeq[CSVColumn], path: Path): Unit = {
    val out = Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    try {
      makeCSV(data, columns, out)
    } catch {
      case e: Exception => e.printStackTrace()
    }
    out.close()
  }

  def saveColumns(columns: IndexedSeq[CSVColumn], path: Path): Unit = {
    val out = Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    try {
      val ps = new PrintStream(out, true, "utf-8")
      ps.println(columns.filter(_.enabled).map(s => "\"" + s.title + "\"").mkString("\n"))
    } catch {
      case e: Exception => e.printStackTrace()
    }
    out.close()
  }
}
