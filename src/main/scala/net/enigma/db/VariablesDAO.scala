package net.enigma.db

import scala.collection.JavaConversions._

import com.datastax.driver.core.Row
import com.datastax.spark.connector.types.TypeConverter
import net.enigma.model.VariableDefinition

/**
 * @author Jacek Lewandowski
 */
object VariablesDAO extends Entity {
  override val table: String = "variables"

  val stringConverter = TypeConverter.forType[String]

  def mapToEntity(row: Row): VariableDefinition = {
    VariableDefinition(
      row.getString("category"),
      row.getInt("id"),
      row.getString("name"),
      row.getString("plus"),
      row.getString("minus")
    )
  }

  def getVariablesDataSet(category: String): List[VariableDefinition] = {
    DBManager.connector.withSessionDo { session ⇒
      val stmt = session.prepare(
        // @formatter:off
        s"""
           |SELECT * FROM "$keyspace"."$table"
           |WHERE category = ?
         """.stripMargin).bind(category)
        // @formatter:on
        val rows = session.execute(stmt).iterator()
      rows.map(mapToEntity).toList
    }
  }

}
