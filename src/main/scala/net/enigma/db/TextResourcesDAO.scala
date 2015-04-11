package net.enigma.db

import java.util.concurrent.TimeUnit

import com.google.common.cache.{CacheBuilder, CacheLoader}

/**
 * @author Jacek Lewandowski
 */
object TextResourcesDAO extends Entity {

  override val table: String = "text_resources"

  private val cache = CacheBuilder.newBuilder()
    .concurrencyLevel(100)
    .expireAfterWrite(5, TimeUnit.SECONDS)
    .build(
      new CacheLoader[String, Option[String]]() {
        override def load(key: String): Option[String] = {
          DBManager.connector.withSessionDo { session ⇒
            val stmt = session.prepare(
              s"""
                 |SELECT value FROM "$keyspace"."$table" WHERE key = ?
             """.stripMargin).bind(key)

            Option(session.execute(stmt).one()).map(_.getString(0))
          }

        }
      })

  def get(key: String): Option[String] = cache.get(key)
}
