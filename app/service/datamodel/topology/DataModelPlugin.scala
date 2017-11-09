package service.datamodel.topology

import javax.inject.{Inject, Singleton}

import service.{DSECheckPlugin, FutureLimiter, ParsingUtils}

import scala.util.matching.Regex

/**
  * Check data model.
  */
@Singleton
class DataModelPlugin @Inject()(nodeRepository: CassandraTableRepository) extends DSECheckPlugin {
  override def process(reportId: String, tmpPath: String): Unit = {
    implicit val limiter = new FutureLimiter[Void]()
    forEachNode(tmpPath, (nodePath, nodeIp) => {
      val cfstat = scala.io.Source.fromFile(s"$tmpPath/$nodeIp/cfstat").mkString
      val tables = extractCassandraTable(cfstat)
      //      var keyspace = ""
      //      val node = new CassandraTable(reportId, nodeIp)
      //      lines.foreach(line => {
      //        if (line.startsWith("Keyspace")) {
      //          keyspace = line.substring(line.indexOf(":") + 1)
      //        } else if (line.contains("CassandraTable:")) {
      //          node.setRack(line.substring(line.indexOf(":") + 1))
      //        } else if (line.startsWith("ID")) {
      //          node.setId(line.substring(line.indexOf(":") + 1))
      //        }
      //      })
      //tables.foreach(table => limiter.add(nodeRepository.saveAsync(table)))
    })
    limiter.waitAll()
  }


  private[topology] def extractCassandraTable(cfstat: String) = {
    val keyspaces = splitKeyspaces(cfstat)




//    keyspaces.flatMap(keyspace => {
//      tableMatcher.findAllIn(keyspace).map(s => {
//        new CassandraTable()
//      })
//    })
  }

  private[topology] def extractTable(table: String) = {
    val tableRegexp ="""(?s)\s*Table: (.*)
       |\s*SSTable count: (.*)
       |\s*Space used \(live\): (.*)
       |\s*Space used \(total\): (.*)
       |\s*Space used by snapshots \(total\): (.*)
       |\s*Off heap memory used \(total\): (.*)
       |\s*SSTable Compression Ratio: (.*)
       |\s*Number of keys \(estimate\): (.*)
       |\s*Memtable cell count: (.*)
       |\s*Memtable data size: (.*)
       |\s*Memtable off heap memory used: (.*)
       |\s*Memtable switch count: (.*)
       |\s*Local read count: (.*)
       |\s*Local read latency: (.*) ms
       |\s*Local write count: (.*)
       |\s*Local write latency: (.*) ms
       |\s*Pending flushes: (.*)
       |\s*Bloom filter false positives: (.*)
       |\s*Bloom filter false ratio: (.*)
       |\s*Bloom filter space used: (.*)
       |\s*Bloom filter off heap memory used: (.*)
       |\s*Index summary off heap memory used: (.*)
       |\s*Compression metadata off heap memory used: (.*)
       |\s*Compacted partition minimum bytes: (.*)
       |\s*Compacted partition maximum bytes: (.*)
       |\s*Compacted partition mean bytes: (.*)
       |\s*Average live cells per slice \(last five minutes\): (.*)
       |\s*Maximum live cells per slice \(last five minutes\): (.*)
       |\s*Average tombstones per slice \(last five minutes\): (.*)
       |\s*Maximum tombstones per slice \(last five minutes\): (.*)
       |\s*Dropped Mutations: (.*)""".stripMargin.r
    val m: Regex.Match = tableRegexp.findFirstMatchIn(table).get
    new CassandraTable().setName(m.group(1))
  }

  private[topology] def splitKeyspaces(cfstat: String) = {
    val ksReports = cfstat.split("----------------").filter(!_.startsWith("Total number of tables")).filter(_.contains("Keyspace"))
    ksReports.map(extractKeyspaceData(_))
  }

  private[topology] def extractKeyspaceData(ksReport: String) = {
    val ksRegex =
      """(?s)\s?Keyspace : (.*)
        |\s?Read Count: (.*)
        |\s?Read Latency: (.*) ms.
        |\s?Write Count: (.*)
        |\s?Write Latency: (.*) ms.
        |\s?Pending Flushes: (.*?)$*
        |(.*)""".stripMargin.r
    val m: Regex.Match = ksRegex.findFirstMatchIn(ksReport).get
    val ks = new CassandraKeyspace()
      .setName(m.group(1))
      .setReadCount(ParsingUtils.toJavaLong(m.group(2)))
      .setReadLatency(ParsingUtils.toJavaInteger(m.group(3)))
      .setWriteCount(ParsingUtils.toJavaLong(m.group(4)))
      .setWriteLatency(ParsingUtils.toJavaInteger(m.group(5)))
      .setPendingFlushes(ParsingUtils.toJavaInteger(m.group(6)))
    (ks, m.group(7))
  }

}
