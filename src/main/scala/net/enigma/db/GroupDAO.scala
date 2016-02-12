package net.enigma.db

import java.io.{OutputStream, PrintStream}

import scala.collection.JavaConversions._

import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.slf4j.LoggerFactory

import com.datastax.spark.connector.types.TypeConverter
import net.enigma.model.Group

/**
 * @author Jacek Lewandowski
 */
object GroupDAO extends Entity {
  private val logger = LoggerFactory.getLogger(GroupDAO.getClass)

  override val table = "groups"

  val stringConverter = TypeConverter.forType[String]

  def getGroup(code: String): Option[Group] = {
    DBManager.connector.withSessionDo { session ⇒
      val rs = session.execute(
        s"SELECT code, category FROM $keyspace.$table WHERE code = ?", code)
      Option(rs.one()) map {
        case row ⇒
          Group(
            code = row.getString("code"),
            category = row.getString("category")
          )
      }
    }
  }

  def addGroup(code: String, category: String): Unit = {
    DBManager.connector.withSessionDo { session ⇒
      session.execute(s"INSERT INTO $keyspace.$table (code, category) VALUES (?, ?)", code, category)
    }
  }

  def backup(out: OutputStream): Unit = {
    import org.json4s.native.Serialization.write
    implicit val formats = Serialization.formats(NoTypeHints)

    val printStream = new PrintStream(out, true, "utf-8")
    DBManager.connector.withSessionDo { session =>
      val rs = session.execute(
        s"SELECT code, category FROM $keyspace.$table")
      rs.iterator().foreach { row ⇒
        val data = Map(
          "code" -> row.getString("code"),
          "category" -> row.getString("category")
        )
        printStream.println(write(data))
      }
    }
  }

}
