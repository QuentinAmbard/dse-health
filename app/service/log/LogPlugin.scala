package service.log

import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalDateTime, ZoneOffset}
import java.util.UUID
import javax.inject.{Inject, Singleton}

import com.datastax.driver.core.utils.UUIDs
import com.google.common.util.concurrent.ListenableFuture
import io.thekraken.grok.api.Grok
import service.{DSECheckPlugin, FutureLimiter, TimeUUIDUtils}
import org.joda.time.format.ISODateTimeFormat

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by quentin on 07/06/17.
  */
@Singleton
class LogPlugin @Inject()(logRepository: LogRepository) extends DSECheckPlugin {
  val grok = new Grok()
  grok.addPatternFromReader(new InputStreamReader(getClass.getResourceAsStream("/log-pattern")))
  //%-5level [%thread] %date{'{ISO8601}'} %X{'{service}'} %F:%L - %msg%n
  grok.compile("""%{LOGLEVEL:logLevel}\s*\[%{DATA:thread}\]\s*%{JAVALOGBACKTIMESTAMP:timestamp}\s*%{DATA:class}:%{DATA:line}\s*\-\s*%{GREEDYDATA:message}""")

  override def process(reportId: String, tmpPath: String): Unit = {
    val df = ISODateTimeFormat.dateTimeNoMillis
    forEachNode(tmpPath, (path, nodeIp) => {
      val source = scala.io.Source.fromFile(s"$tmpPath/$nodeIp/logs/system.log")
      val limiter = new FutureLimiter[Void](100)
      try source.getLines().foreach(line => {
        val gm = grok.`match`(line)
        gm.captures()
        if(!gm.isNull) {
          val map = gm.toMap
          val date = LocalDateTime.of(Integer.valueOf(map.get("YEAR").toString), Integer.valueOf(map.get("MONTHNUM").toString), Integer.valueOf(map.get("MONTHDAY").toString),
            Integer.valueOf(map.get("HOUR").toString), Integer.valueOf(map.get("MINUTE").toString), Integer.valueOf(map.get("SECOND").toString), Integer.valueOf(map.get("MILLISECOND").toString) * 1000 * 1000)
          val uuid = TimeUUIDUtils.fromTimestampMs(date.toInstant(ZoneOffset.UTC).toEpochMilli)
          val log = new Log(reportId, nodeIp, uuid, map.get("logLevel").toString, map.get("thread").toString, map.get("class").toString, map.get("line").toString, map.get("message").toString)
          limiter.add(logRepository.saveAsync(log))
        }
      }) finally source.close()
      limiter.waitAll()
    })
  }
}
