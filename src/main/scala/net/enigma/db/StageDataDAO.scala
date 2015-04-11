package net.enigma.db

import scala.collection.JavaConversions._

import com.datastax.driver.core.Row
import com.datastax.spark.connector.types.TypeConverter
import net.enigma.model.StageData

/**
 * @author Jacek Lewandowski
 */
object StageDataDAO extends Entity {
  override val table: String = "stage_data"

  val stringConverter = TypeConverter.forType[String]

  def mapToEntity(row: Row): StageData = {
    StageData(
      usercode = row.getString("user_code"),
      stage = row.getString("stage"),
      key = row.getString("key"),
      idx = row.getInt("idx"),
      data = row.getString("data")
    )
  }

  def getStageData(usercode: String, stage: String, key: String, idx: Int = 0): Option[StageData] = {
    DBManager.connector.withSessionDo { session ⇒
      val stmt = session.prepare(
        // @formatter:off
        s"""
           |SELECT * FROM "$keyspace"."$table"
           |WHERE user_code = ? AND stage = ? AND key = ? AND idx = ?
         """.stripMargin).bind(usercode, stage, key, idx: Integer)
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
  }

}
