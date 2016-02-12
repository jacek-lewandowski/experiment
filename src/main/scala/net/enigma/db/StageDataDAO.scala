package net.enigma.db

import java.io.{OutputStream, PrintStream}
import java.util.concurrent.TimeUnit

import scala.collection.JavaConversions._

import com.google.common.cache.{CacheBuilder, CacheLoader}
import org.json4s.NoTypeHints
import org.json4s.ext.EnumNameSerializer
import org.json4s.native.Serialization

import com.datastax.driver.core.Row
import com.datastax.spark.connector.types.TypeConverter
import net.enigma.model.TrialStageInfo.IterationState
import net.enigma.model.VariablesStageInfo.VariablesState
import net.enigma.model.{StageData, TrialAnswer}

/**
 * @author Jacek Lewandowski
 */
object StageDataDAO extends Entity {
  override val table: String = "stage_data"

  val stringConverter = TypeConverter.forType[String]

  private val cache = CacheBuilder.newBuilder()
    .concurrencyLevel(16)
    .expireAfterWrite(10, TimeUnit.MINUTES)
    .initialCapacity(1000)
    .maximumSize(10000)
    .build[(String, String, String, Int), Option[StageData]](new CacheLoader[(String, String, String, Int), Option[StageData]]() {
    override def load(k: (String, String, String, Int)): Option[StageData] = {
      loadStageData(k._1, k._2, k._3, k._4)
    }
  })

  object Trial {
    implicit val formats = Serialization.formats(NoTypeHints) ++ Seq(
      new EnumNameSerializer(TrialAnswer),
      new EnumNameSerializer(IterationState)
    )

    val stageID = "trial"
    val iterationID = "iteration"
    val stageInfoID = "stageInfo"
  }

  object Lottery {
    implicit val formats = Serialization.formats(NoTypeHints)

    val stageID = "lottery"
    val stageInfoID = "stageInfo"
  }

  object MissingVariables {
    implicit val formats = Serialization.formats(NoTypeHints)

    val stageID = "missingVariables"
    val stageInfoID = "stageInfo"
  }

  object Justifications {
    implicit val formats = Serialization.formats(NoTypeHints)

    val stageID = "justifications"
    val stageInfoID = "stageInfo"
  }

  object Variables {
    implicit val formats = Serialization.formats(NoTypeHints) ++ Seq(
      new EnumNameSerializer(VariablesState)
    )

    val stageID = "variables"
    val stageInfoID = "stageInfo"
  }

  object Questionnnaire {
    implicit val formats = Serialization.formats(NoTypeHints)

    val stageID = "questionnaire"
    val answerID = "answer"
  }

  object PersonalData {
    implicit val formats = Serialization.formats(NoTypeHints)

    val stageID = "personalData"
    val answerID = "answer"
  }


  def mapToEntity(row: Row): StageData = {
    StageData(
      usercode = row.getString("user_code"),
      stage = row.getString("stage"),
      key = row.getString("key"),
      idx = row.getInt("idx"),
      data = row.getString("data")
    )
  }

  def getStageData(userCode: String, stage: String, key: String, idx: Int = 0): Option[StageData] = {
    cache.get((userCode, stage, key, idx))
  }

  private[StageDataDAO] def loadStageData(userCode: String, stage: String, key: String, idx: Int = 0): Option[StageData] = {
    DBManager.connector.withSessionDo { session ⇒
      val stmt = session.prepare(
        // @formatter:off
        s"""
           |SELECT * FROM "$keyspace"."$table"
           |WHERE user_code = ? AND stage = ? AND key = ? AND idx = ?
         """.stripMargin).bind(userCode, stage, key, idx: Integer)
        // @formatter:on
      session.execute(stmt).iterator().take(1).toSeq.headOption.map(mapToEntity)
    }
  }

  def saveStageData(stageData: StageData): Unit = {
    DBManager.connector.withSessionDo { session ⇒
      val stmt = session.prepare(
        // @formatter:off
        s"""
           |INSERT INTO "$keyspace"."$table" (user_code, stage, key, idx, data)
           |VALUES (?, ?, ?, ?, ?)
         """.stripMargin)
        // @formatter:on
        .bind(stageData.usercode, stageData.stage, stageData.key, stageData.idx: Integer, stageData.data)

      session.execute(stmt)
    }
    cache.put((stageData.usercode, stageData.stage, stageData.key, stageData.idx), Some(stageData))
  }

  def backup(out: OutputStream): Unit = {
    import org.json4s.native.Serialization.{read, write}
    import org.json4s.native.JsonMethods._
    implicit val formats = Serialization.formats(NoTypeHints)

    val printStream = new PrintStream(out, true, "utf-8")
    DBManager.connector.withSessionDo { session =>
      val rs = session.execute(
        s"SELECT * FROM $keyspace.$table")
      rs.iterator().map(mapToEntity).foreach { row ⇒
        val common = Map(
          "user_code" -> row.usercode,
          "stage" -> row.stage,
          "key" -> row.key,
          "idx" -> row.idx
        )
        val data = parse(row.data, useBigDecimalForDouble = false)
        val toWrite = common + ("data" -> data)
        printStream.println(write(toWrite))
      }
    }
  }


}
