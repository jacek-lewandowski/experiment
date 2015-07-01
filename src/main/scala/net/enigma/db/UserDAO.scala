package net.enigma.db

import java.io.{PrintStream, OutputStream}

import scala.collection.JavaConversions._

import com.google.common.collect.Sets
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.slf4j.LoggerFactory

import com.datastax.spark.connector.types.TypeConverter
import net.enigma.model.User

/**
 * @author Jacek Lewandowski
 */
object UserDAO extends Entity {
  private val logger = LoggerFactory.getLogger(UserDAO.getClass)

  override val table = "users"

  val stringConverter = TypeConverter.forType[String]

  def getUser(code: String): Option[User] = {
    DBManager.connector.withSessionDo { session ⇒
      val rs = session.execute(
        s"SELECT code, category, email_address, completed_stages, current_stage FROM $keyspace.$table WHERE code = ?", code)
      Option(rs.one()) map {
        case row ⇒
          User(
            code = row.getString("code"),
            category = row.getString("category"),
            email = Option(row.getString("email_address")),
            currentStage = Option(row.getString("current_stage")),
            completedStages = row.getSet("completed_stages", classOf[String]).toSet
          )
      }
    }
  }

  def setEmailAddress(code: String, emailAddress: String): Unit = {
    DBManager.connector.withSessionDo { session ⇒
      session.execute(s"UPDATE $keyspace.$table SET email_address = ? WHERE code = ?", emailAddress, code)
    }
  }

  def getCurrentStage(code: String): Option[String] = {
    DBManager.connector.withSessionDo { session ⇒
      val rs = session.execute(s"SELECT current_stage FROM $keyspace.$table WHERE code = ?", code)
      Option(rs.one()) match {
        case Some(row) if !row.isNull(0) ⇒ Some(row.getString(0))
        case _ ⇒ None
      }
    }
  }

  def setCurrentStage(code: String, stageId: String): Unit = {
    DBManager.connector.withSessionDo { session ⇒
      session.execute(s"UPDATE $keyspace.$table SET current_stage = ? WHERE code = ?", stageId, code)
    }
  }

  def addUser(code: String, category: String): Unit = {
    DBManager.connector.withSessionDo { session ⇒
      session.execute(s"INSERT INTO $keyspace.$table (code, category) VALUES (?, ?)", code, category)
    }
  }

  def addToUserCompletedStages(userCode: String, stageId: String): Unit = {
    logger.info(s"addToUserCompletedStages($userCode, $stageId)")
    DBManager.connector.withSessionDo { session ⇒
      session.execute(
        s"UPDATE $keyspace.$table SET completed_stages = completed_stages + ? WHERE code = ?",
        Sets.newHashSet(stageId), userCode
      )
    }
  }

  def getUserCompletedStages(userCode: String): Set[String] = {
    DBManager.connector.withSessionDo { session ⇒
      val rs = session.execute(s"SELECT completed_stages FROM $keyspace.$table WHERE code = ?", userCode)
      Option(rs.one()) match {
        case Some(row) ⇒
          row.getSet("completed_stages", classOf[String]).toSet
        case _ ⇒ Set.empty
      }
    }
  }

  def getAllUsers(): Seq[User] = {
    DBManager.connector.withSessionDo { session =>
      val rs = session.execute(
        s"SELECT code, category, email_address, completed_stages, current_stage FROM $keyspace.$table")
      rs.all() map {
        case row ⇒
          User(
            code = row.getString("code"),
            category = row.getString("category"),
            email = Option(row.getString("email_address")),
            currentStage = Option(row.getString("current_stage")),
            completedStages = row.getSet("completed_stages", classOf[String]).toSet
          )
      }
    }
  }

  def backup(out: OutputStream): Unit = {
    import org.json4s.native.Serialization.write
    implicit val formats = Serialization.formats(NoTypeHints)

    val printStream = new PrintStream(out, true, "utf-8")
    DBManager.connector.withSessionDo { session =>
      val rs = session.execute(
        s"SELECT code, category, email_address, completed_stages, current_stage FROM $keyspace.$table")
      rs.iterator().foreach { row ⇒
        val data = Map(
          "code" -> row.getString("code"),
          "category" -> row.getString("category"),
          "email" -> Option(row.getString("email_address")),
          "currentStage" -> Option(row.getString("current_stage")),
          "completedStages" -> row.getSet("completed_stages", classOf[String]).toSet
        )
        printStream.println(write(data))
      }
    }
  }

}
