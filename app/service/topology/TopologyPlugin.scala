package service.topology

import java.util.UUID
import javax.inject.{Inject, Singleton}

import service.{DSECheckPlugin, FutureLimiter, ParsingUtils}

/**
  * Created by quentin on 07/06/17.
  */
@Singleton
class TopologyPlugin @Inject()(nodeRepository: NodeRepository, nodeStatusRepository: NodeStatusRepository) extends DSECheckPlugin {

  override def process(reportId: String, tmpPath: String): Unit = {
    val limiter = new FutureLimiter[Void]()
    forEachNode(tmpPath, (nodePath, nodeIp) => {
      val infoLine = readNodetoolInfo(tmpPath, nodeIp)
      val node: Node = extractNodeFromInfo(reportId, nodeIp, infoLine)
      limiter.add(nodeRepository.saveAsync(node))

      val statusLines = readNodetoolStatus(tmpPath, nodeIp)
      val dcs = splitDC(statusLines.mkString)
      dcs.foreach{case (dcName, dcString) => {
        extractNodeStatus(reportId, node.getId, dcName, dcString).foreach(node => {
          limiter.add(nodeStatusRepository.saveAsync(node))
        })
      }}
    })
    limiter.waitAll()
  }

  def extractNodeFromInfo(reportId: String, nodeId: UUID, lines: Iterator[String]) = {
    val node = new Node(reportId, nodeId)
    lines.foreach(line => {
      if (line.startsWith("Data Center")) {
        node.setDc(line.substring(line.indexOf(":") + 1).trim)
      } else if (line.startsWith("Rack")) {
        node.setRack(line.substring(line.indexOf(":") + 1).trim)
      } else if (line.startsWith("ID")) {
        assert(nodeId == UUID.fromString(line.substring(line.indexOf(":") + 1).trim))
      } else if (line.startsWith("Percent Repaired")) {
        node.setPercentRepaired(ParsingUtils.toJavaFloat(line.substring(line.indexOf(":") + 1, line.length-1).trim))
      } else if (line.startsWith("Exceptions")) {
        node.setException(ParsingUtils.toJavaInteger(line.substring(line.indexOf(":") + 1).trim))
      }
    })
    node
  }

  /**
    * @param nodetoolStr
    * @return a list of [(nameDC, nodetoolResultForThisDC), (nameDC, nodetoolResultForThisDC)]
    */
  def splitDC(nodetoolStr: String) = {
    val nodetoolByDCs = nodetoolStr.split("""(?=\s?Datacenter:)""").filter(_.size > 3)
    val dcRegex = """\s?Datacenter: (.*)""".r
    nodetoolByDCs.map(nodetoolByDC => {
      val nameDC = dcRegex.findFirstMatchIn(nodetoolByDC).get.group(0)
      (nameDC, nodetoolByDC)
    })
  }


  def extractNodeStatus(reportId: String, nodeId: UUID, dc: String, nodeStr: String) = {
    val lines = nodeStr.lines
    val nodeStatusRegexp = """([UD])([NLJM]) ([0-9.]*) ([0-9.,]*) ([GTKMB]*) ([0-9]*) ([0-9,]*)% ([0-9abcdef\-]*) (.*)""".r
    lines.map(line => {
      val m = nodeStatusRegexp.findFirstMatchIn(line)
      m.map(m => {
        new NodeStatus(reportId, nodeId)
          .setDc(dc)
          .setStatus(m.group(1))
          .setState(m.group(2))
          .setTargetIp(m.group(3))
          .setLoad(ParsingUtils.toJavaFloat(m.group(4)))
          .setLoadUnit(m.group(5))
          .setToken(ParsingUtils.toJavaInteger(m.group(6)))
          .setEffective(ParsingUtils.toJavaFloat(m.group(7)))
          .setTargetId(UUID.fromString(m.group(8)))
          .setRack(m.group(9))
      })
    }).flatten
  }

}
