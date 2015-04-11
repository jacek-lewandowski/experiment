package net.enigma.db

import scala.collection.JavaConversions._

import org.slf4j.LoggerFactory

import com.datastax.driver.core.Row
import com.datastax.spark.connector.types.TypeConverter
import net.enigma.model.Variable

/**
 * @author Jacek Lewandowski
 */
object UserVariablesDAO extends Entity {

  private val logger = LoggerFactory.getLogger(UserVariablesDAO.getClass)

  override val table: String = "user_variables"

  val stringConverter = TypeConverter.forType[String]

  def saveUserVariables(category: String, userId: String, stageId: String, variables: Seq[Variable]) = {
    DBManager.connector.withSessionDo { session ⇒
      val stmt = session.prepare(
        s"""
            INSERT INTO $keyspace.$table (category, user_code, stage_id, id, name, ordinal_number, score)
            VALUES (?, ?, ?, ?, ?, ? ,?)
        """)

      for (variable ← variables) {
        session.execute(stmt.bind(mapToRow(category, userId, stageId, variable): _*))
      }
    }
  }

  def getUserVariables(category: String, userCode: String, stageId: String): List[Variable] = {
    DBManager.connector.withSessionDo { session ⇒
      val rows = session.execute(
        s"SELECT * FROM $keyspace.$table WHERE category = ? AND stage_id = ? AND user_code = ?",
        category, stageId, userCode)

      rows.map(mapToEntity).toList
    }
  }

  def mapToEntity(row: Row): Variable = {
    Variable(
      row.getInt("id"),
      row.getString("name"),
      if (row.isNull("ordinal_number")) None else Some(row.getInt("ordinal_number")),
      if (row.isNull("score")) None else Some(row.getInt("score"))
    )
  }

  def mapToRow(category: String, userCode: String, stageId: String, entity: Variable): Array[AnyRef] = {
    val result = Array[AnyRef](
      category,
      userCode,
      stageId,
      entity.id: Integer,
      entity.title,
      entity.ordinalNumber.map(x ⇒ x: Integer).orNull,
      entity.score.map(x ⇒ x: Integer).orNull
    )
    logger.info(s"mapToRow($category, $userCode, $stageId, $entity)")
    result
  }

}
