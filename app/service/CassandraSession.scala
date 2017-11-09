package service

import javax.inject.Singleton
import com.datastax.driver.core.Cluster

@Singleton
class CassandraSession {
  val cluster = Cluster.builder.addContactPoints("127.0.0.1").build
  val session = cluster.connect
}
