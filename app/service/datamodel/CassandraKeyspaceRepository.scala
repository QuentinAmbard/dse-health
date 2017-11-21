package service.datamodel

import javax.inject.{Inject, Singleton}

import com.datastax.driver.mapping.{Mapper, MappingManager}
import com.google.common.util.concurrent.ListenableFuture
import service.CassandraSession

@Singleton
class CassandraKeyspaceRepository() {
  var mapper: Option[Mapper[CassandraKeyspace]] = None

  @Inject
  def init(cSession: CassandraSession): Unit = {
    val manager = new MappingManager(cSession.session)
    mapper = Some(manager.mapper(classOf[CassandraKeyspace]))
  }

  def save(log: CassandraKeyspace): Unit = {
    mapper.get.save(log)
  }

  def saveAsync(table: CassandraKeyspace): ListenableFuture[Void] = {
    mapper.get.saveAsync(table)
  }

  def delete(reportId: String): Unit = {
    mapper.get.delete(reportId)
  }

}
