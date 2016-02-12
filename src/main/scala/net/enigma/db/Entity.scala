package net.enigma.db

import com.datastax.driver.core.ProtocolVersion
import com.datastax.spark.connector.cql.TableDef


/**
 * @author Jacek Lewandowski
 */
trait Entity {
  val keyspace: String = "experiment"
  val table: String

  lazy val tableDef: TableDef = DBManager.schema.keyspaceByName(keyspace).tableByName(table)

  implicit val protocolVersion: ProtocolVersion = ProtocolVersion.NEWEST_SUPPORTED

}
