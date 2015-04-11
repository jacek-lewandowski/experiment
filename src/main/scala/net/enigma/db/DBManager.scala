package net.enigma.db

import java.net.InetAddress

import com.datastax.spark.connector.cql.{Schema, CassandraConnector}

/**
 * @author Jacek Lewandowski
 */
object DBManager {
  val connector = CassandraConnector(hosts = Set(InetAddress.getLoopbackAddress))

  lazy val schema = Schema.fromCassandra(connector)


}
