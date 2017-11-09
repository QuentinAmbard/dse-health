package service.topology

import java.io.InputStreamReader
import java.time.{LocalDateTime, ZoneOffset}
import javax.inject.{Inject, Singleton}

import com.google.common.util.concurrent.RateLimiter
import io.thekraken.grok.api.Grok
import org.joda.time.format.ISODateTimeFormat
import service.{DSECheckPlugin, FutureLimiter, TimeUUIDUtils}

/**
  * Created by quentin on 07/06/17.
  */
@Singleton
class TopologyPlugin @Inject()(nodeRepository: NodeRepository) extends DSECheckPlugin {
  override def process(reportId: String, tmpPath: String): Unit = {
    val limiter = new FutureLimiter[Void]()
    forEachNode(tmpPath, (nodePath, nodeIp) => {
      val lines = scala.io.Source.fromFile(s"$tmpPath/$nodeIp/info").getLines()
      val node = new Node(reportId, nodeIp)
      lines.foreach(line => {
        if (line.startsWith("Data Center")) {
          node.setDc(line.substring(line.indexOf(":")+1))
        } else if (line.startsWith("Rack")) {
          node.setRack(line.substring(line.indexOf(":")+1))
        } else if (line.startsWith("ID")) {
          node.setId(line.substring(line.indexOf(":")+1))
        }
      })
      limiter.add(nodeRepository.saveAsync(node))
    })
    limiter.waitAll()
  }
}
