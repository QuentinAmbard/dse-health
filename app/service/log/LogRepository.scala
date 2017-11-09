package service.log

import javax.inject.{Inject, Singleton}

import com.datastax.driver.mapping.MappingManager
import com.google.common.util.concurrent.ListenableFuture
import service.CassandraSession

@Singleton
class LogRepository @Inject() (cSession: CassandraSession) {
  val manager = new MappingManager(cSession.session)
  val mapper = manager.mapper(classOf[Log])

  def save(log: Log): Unit = {
    mapper.save(log)
  }

  def saveAsync(log: Log): ListenableFuture[Void] = {
    mapper.saveAsync(log)
  }

  def delete(reportId: String, nodeId: String): Unit = {
    mapper.delete(reportId, nodeId)
  }

}
