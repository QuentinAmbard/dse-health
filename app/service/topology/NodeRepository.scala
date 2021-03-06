package service.topology

import javax.inject.{Inject, Singleton}

import com.datastax.driver.mapping.{Mapper, MappingManager}
import com.google.common.util.concurrent.ListenableFuture
import service.CassandraSession
import service.datamodel.CassandraTable

@Singleton
class NodeRepository @Inject()(cSession: CassandraSession) {
  var mapper: Option[Mapper[Node]] = None
  @Inject
  def init(cSession: CassandraSession): Unit ={
    val manager = new MappingManager(cSession.session)
    mapper = Some(manager.mapper(classOf[Node]))
  }

  def save(log: Node): Unit = {
    mapper.get.save(log)
  }

  def saveAsync(log: Node): ListenableFuture[Void] = {
    mapper.get.saveAsync(log)
  }

  def delete(reportId: String): Unit = {
    mapper.get.delete(reportId)
  }

}
