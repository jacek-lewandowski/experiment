package net.enigma.db

import scala.collection.JavaConversions._

import com.datastax.driver.core.Row
import com.datastax.spark.connector.types.TypeConverter
import net.enigma.model.Question

/**
 * @author Jacek Lewandowski
 */
object QuestionDAO extends Entity {
  override val table: String = "surveys"

  val stringConverter = TypeConverter.forType[String]

  def mapToEntity(row: Row): Question = {
    Question(
      row.getString("category"),
      row.getInt("id"),
      row.getString("caption"),
      row.getBool("required"),
      row.getString("validator_name"),
      row.getMap[String, String]("validator_params", classOf[String], classOf[String]).toMap
    )
  }

  def getQuestionsSet(set: String): Seq[Question] = {
    DBManager.connector.withSessionDo { session ⇒
      val stmt = session.prepare(
        s"""
           |SELECT * FROM "$keyspace"."$table"
           |WHERE category = ?
         """.stripMargin).bind(set)

      val rows = session.execute(stmt).iterator()
      rows.map(mapToEntity).toSeq
    }
  }

}
