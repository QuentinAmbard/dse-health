package service.datamodel

import javax.inject.{Inject, Singleton}

import com.datastax.driver.mapping.{Mapper, MappingManager}
import com.google.common.util.concurrent.ListenableFuture
import service.{CassandraSession, FutureLimiter}

@Singleton
class CassandraTableRepository() {
  var mapper: Option[Mapper[CassandraTable]] = None
  @Inject
  def init(cSession: CassandraSession): Unit ={
    val manager = new MappingManager(cSession.session)
    mapper = Some(manager.mapper(classOf[CassandraTable]))
  }

  def save(log: CassandraTable): Unit = {
    mapper.get.save(log)
  }

  def saveAsync(table: CassandraTable): ListenableFuture[Void]  = {
    mapper.get.saveAsync(table)
  }

  def delete(reportId: String): Unit = {
    mapper.get.delete(reportId)
  }

}
