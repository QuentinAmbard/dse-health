package service.datamodel

import javax.inject.{Inject, Singleton}

import service.{DSECheckPlugin, FutureLimiter, ParsingUtils}

import scala.util.matching.Regex

/**
  * Check data model.
  */
@Singleton
class DataModelPlugin @Inject()(tableRepository: CassandraTableRepository, keyspaceRepository: CassandraKeyspaceRepository) extends DSECheckPlugin {
  override def process(reportId: String, tmpPath: String): Unit = {
    implicit val limiter = new FutureLimiter[Void]()
    forEachNode(tmpPath, (nodePath, nodeId) => {
      val cfstat = readTablestat(tmpPath, nodeId)
      val keyspacesAndTables = extractCassandraTable(cfstat, reportId)
      keyspacesAndTables.foreach{case (keyspace, tables)=> {
        limiter.add(keyspaceRepository.saveAsync(keyspace))
        tables.foreach(table => {
          limiter.add(tableRepository.saveAsync(table))
        })
      }}
    })
    limiter.waitAll()
  }


  def extractCassandraTable(cfstat: String, reportId:String) = {
    val keyspaces: Array[(CassandraKeyspace, String)] = splitKeyspaces(cfstat, reportId)
    keyspaces.map{case (ks, tablesStr) => {
      val tables: Array[CassandraTable] = splitTables(tablesStr).map(extractTable(_, reportId))
      (ks -> tables)
    }}.toMap
  }

  def extractTable(table: String, reportId: String) = {
    val tableRegexp ="""(?s)\s*Table( \(index\))?: (.*)
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
       |\s*Percent repaired: (.*)
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
    val tbl = new CassandraTable().setName(m.group(2))
    tbl.setIndex(m.group(1) == null )
    tbl
    .setSstableCount(ParsingUtils.toJavaLong(m.group(3)))
    .setSpaceUsedLive(ParsingUtils.toJavaLong(m.group(4)))
    .setSpaceUsedTotal(ParsingUtils.toJavaLong(m.group(5)))
    .setSpaceUsedSnapshot(ParsingUtils.toJavaLong(m.group(6)))
    .setOffHeapMemoryUsed(ParsingUtils.toJavaLong(m.group(7)))
    .setCompressionRation(ParsingUtils.toJavaFloat(m.group(8)))
    .setKeyNumber(ParsingUtils.toJavaLong(m.group(9)))
    .setMemtableCellCount(ParsingUtils.toJavaLong(m.group(10)))
    .setMemtableDataSize(ParsingUtils.toJavaLong(m.group(11)))
    .setMemtableOffHeapUsed(ParsingUtils.toJavaLong(m.group(12)))
    .setMemtableSwitchCount(ParsingUtils.toJavaLong(m.group(13)))
    .setLocalReadCount(ParsingUtils.toJavaLong(m.group(14)))
    .setLocalReadLatency(ParsingUtils.toJavaFloat(m.group(15)))
    .setLocalWriteCount(ParsingUtils.toJavaLong(m.group(16)))
    .setLocalWriteLatency(ParsingUtils.toJavaFloat(m.group(17)))
    .setPendingFlushes(ParsingUtils.toJavaLong(m.group(18)))
    .setPercentRepaired(ParsingUtils.toJavaFloat(m.group(19)))
    .setBloomFilterFalsePositive(ParsingUtils.toJavaLong(m.group(20)))
    .setBloomFilterFalseRatio(ParsingUtils.toJavaFloat(m.group(21)))
    .setBloomFilterSpaceUsed(ParsingUtils.toJavaLong(m.group(22)))
    .setBloomFilterOffHeapUsed(ParsingUtils.toJavaLong(m.group(23)))
    .setIndexSummaryOffHeapUsed(ParsingUtils.toJavaLong(m.group(24)))
    .setCompressionMetadataOffHeapUsed(ParsingUtils.toJavaLong(m.group(25)))
    .setPartitionMinBytes(ParsingUtils.toJavaLong(m.group(26)))
    .setPartitionMaxBytes(ParsingUtils.toJavaLong(m.group(27)))
    .setPartitionMeanBytes(ParsingUtils.toJavaLong(m.group(28)))
    .setAverageLiveCellPerSlice(ParsingUtils.toJavaFloat(m.group(29)))
    .setMaximumLiveCellPerSlice(ParsingUtils.toJavaLong(m.group(30)))
    .setAverageTombstonePerSlice(ParsingUtils.toJavaFloat(m.group(31)))
    .setMaximumTombstonePerSlice(ParsingUtils.toJavaLong(m.group(32)))
    .setDroppedMutation(ParsingUtils.toJavaLong(m.group(33)))

  }

  def splitTables(tables: String) = {
    //Filter on 5 to remove first empty line due tu \n \t in the regex
    tables.split("""(?=\sTable[: ])""").filter(_.size>3)
  }

  def splitKeyspaces(cfstat: String, reportId:String): Array[(CassandraKeyspace, String)] = {
    val ksReports = cfstat.split("----------------").filter(!_.startsWith("Total number of tables")).filter(_.contains("Keyspace"))
    ksReports.map(extractKeyspaceData(_, reportId))
  }

  def extractKeyspaceData(ksReport: String, reportId: String) = {
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
      .setReportId(reportId)
      .setName(m.group(1))
      .setReadCount(ParsingUtils.toJavaLong(m.group(2)))
      .setReadLatency(ParsingUtils.toJavaInteger(m.group(3)))
      .setWriteCount(ParsingUtils.toJavaLong(m.group(4)))
      .setWriteLatency(ParsingUtils.toJavaInteger(m.group(5)))
      .setPendingFlushes(ParsingUtils.toJavaInteger(m.group(6)))
    (ks, m.group(7))
  }

}
