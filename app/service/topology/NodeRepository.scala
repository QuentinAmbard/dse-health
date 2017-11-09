package service.topology

import javax.inject.{Inject, Singleton}

import com.datastax.driver.mapping.MappingManager
import com.google.common.util.concurrent.ListenableFuture
import service.CassandraSession

@Singleton
class NodeRepository @Inject()(cSession: CassandraSession) {
  val manager = new MappingManager(cSession.session)
  val mapper = manager.mapper(classOf[Node])

  def save(log: Node): Unit = {
    mapper.save(log)
  }

  def saveAsync(log: Node): ListenableFuture[Void] = {
    mapper.saveAsync(log)
  }

  def delete(reportId: String): Unit = {
    mapper.delete(reportId)
  }

}
