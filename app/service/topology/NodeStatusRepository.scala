package service.topology

import javax.inject.{Inject, Singleton}

import com.datastax.driver.mapping.{Mapper, MappingManager}
import com.google.common.util.concurrent.ListenableFuture
import service.CassandraSession

@Singleton
class NodeStatusRepository @Inject()(cSession: CassandraSession) {
  var mapper: Option[Mapper[NodeStatus]] = None
  @Inject
  def init(cSession: CassandraSession): Unit ={
    val manager = new MappingManager(cSession.session)
    mapper = Some(manager.mapper(classOf[NodeStatus]))
  }

  def save(log: NodeStatus): Unit = {
    mapper.get.save(log)
  }

  def saveAsync(log: NodeStatus): ListenableFuture[Void] = {
    mapper.get.saveAsync(log)
  }

  def delete(reportId: String): Unit = {
    mapper.get.delete(reportId)
  }

}
