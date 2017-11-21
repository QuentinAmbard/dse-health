package service.datamodel

import org.scalamock.scalatest.MockFactory

/**
  * Created by quentin on 16/07/17.
  */
class DataModelPluginTest extends org.scalatest.FunSuite with MockFactory {
  val lines = scala.io.Source.fromInputStream(getClass.getResourceAsStream("cfstat")).mkString
  val cRepo = mock[CassandraTableRepository]
  val kRepo = mock[CassandraKeyspaceRepository]
  val plugin = new DataModelPlugin(cRepo, kRepo)

  test("test extractCassandraTable") {
    val tableAndKeyspaces: Map[CassandraKeyspace, Array[CassandraTable]] = plugin.extractCassandraTable(lines, "A")
    assert(tableAndKeyspaces.size === 16)
    assert(tableAndKeyspaces.find(_._1.getName == "HiveMetaStore").get._2.size === 1)
    tableAndKeyspaces.filter(_._1.getName == "HiveMetaStore").map(k => assert(k._2(0).getName === "sparkmetastore"))

    assert(tableAndKeyspaces.find(_._1.getName == "system_auth").get._2.size === 4)
    tableAndKeyspaces.filter(_._1.getName == "system_auth").map(k => {
      assert(k._2(0).getName === "resource_role_permissons_index")
      assert(k._2(1).getName === "role_members")
      assert(k._2(2).getName === "role_permissions")
      assert(k._2(3).getName === "roles")
    })
  }

  test("read table stats.") {
    val tbstats =
      """		Table: available_ranges
        |		SSTable count: 1
        |		Space used (live): 2
        |		Space used (total): 3
        |		Space used by snapshots (total): 4
        |		Off heap memory used (total): 5
        |		SSTable Compression Ratio: -1.03
        |		Number of keys (estimate): 6
        |		Memtable cell count: 7
        |		Memtable data size: 8
        |		Memtable off heap memory used: 9
        |		Memtable switch count: 10
        |		Local read count: 11
        |		Local read latency: 12 ms
        |		Local write count: 13
        |		Local write latency: 14 ms
        |		Pending flushes: 15
        |		Percent repaired: 16.2
        |		Bloom filter false positives: 17
        |		Bloom filter false ratio: 0.00001
        |		Bloom filter space used: 18
        |		Bloom filter off heap memory used: 19
        |		Index summary off heap memory used: 20
        |		Compression metadata off heap memory used: 21
        |		Compacted partition minimum bytes: 22
        |		Compacted partition maximum bytes: 23
        |		Compacted partition mean bytes: 24
        |		Average live cells per slice (last five minutes): 25
        |		Maximum live cells per slice (last five minutes): 26
        |		Average tombstones per slice (last five minutes): 27
        |		Maximum tombstones per slice (last five minutes): 28
        |		Dropped Mutations: 29""".stripMargin
    val table = plugin.extractTable(tbstats, "A")
    assert(table.getName === "available_ranges")
    assert(table.getSstableCount === 1)
    assert(table.getSpaceUsedLive === 2)
    assert(table.getSpaceUsedTotal === 3)
    assert(table.getSpaceUsedSnapshot === 4)
    assert(table.getOffHeapMemoryUsed === 5)
    assert(table.getCompressionRation === -1.03F)
    assert(table.getKeyNumber === 6)
    assert(table.getMemtableCellCount === 7)
    assert(table.getMemtableDataSize === 8)
    assert(table.getMemtableOffHeapUsed === 9)
    assert(table.getMemtableSwitchCount === 10)
    assert(table.getLocalReadCount === 11)
    assert(table.getLocalReadLatency === 12F)
    assert(table.getLocalWriteCount === 13)
    assert(table.getLocalWriteLatency === 14F)
    assert(table.getPendingFlushes === 15)
    assert(table.getPercentRepaired === 16.2F)
    assert(table.getBloomFilterFalsePositive === 17)
    assert(table.getBloomFilterFalseRatio === 0.00001F)
    assert(table.getBloomFilterSpaceUsed === 18)
    assert(table.getBloomFilterOffHeapUsed === 19)
    assert(table.getIndexSummaryOffHeapUsed === 20)
    assert(table.getCompressionMetadataOffHeapUsed === 21)
    assert(table.getPartitionMinBytes === 22)
    assert(table.getPartitionMaxBytes === 23)
    assert(table.getPartitionMeanBytes === 24)
    assert(table.getAverageLiveCellPerSlice === 25)
    assert(table.getMaximumLiveCellPerSlice === 26)
    assert(table.getAverageTombstonePerSlice === 27)
    assert(table.getMaximumTombstonePerSlice === 28)
    assert(table.getDroppedMutation === 29)
  }


  test("split keyspaces from cfstats.") {
    val ks = plugin.splitKeyspaces(lines, "A")
    assert(ks.size == 16)
  }

  val beginingTable =
    """		Table: cleanup
      |		SSTable count: 0
      |		Space used (live): 0
      |		Space used (total): 0
      |		Space used by snapshots (total): 0
      |""".stripMargin

  test("extract keyspaces data empty.") {
    val test =
      s"""Keyspace : spark_system
         |	Read Count: 0
         |	Read Latency: NaN ms.
         |	Write Count: 0
         |	Write Latency: NaN ms.
         |	Pending Flushes: 0
         |${beginingTable}""".stripMargin
    val (ks, b) = plugin.extractKeyspaceData(test, "A")
    assert(b === beginingTable)
    assert(ks.getName === "spark_system")
    assert(ks.getReadCount === 0)
    assert(ks.getReadLatency === null)
    assert(ks.getWriteCount === 0)
    assert(ks.getWriteLatency === null)
    assert(ks.getPendingFlushes === 0)
  }

  test("extract keyspaces data.") {
    val test =
      s"""Keyspace : test
         |	Read Count: 12
         |	Read Latency: 153 ms.
         |	Write Count: 99
         |	Write Latency: 36 ms.
         |	Pending Flushes: 44
         |${beginingTable}""".stripMargin
    val (ks, b) = plugin.extractKeyspaceData(test, "A")
    assert(b === beginingTable)
    assert(ks.getName === "test")
    assert(ks.getReadCount === 12)
    assert(ks.getReadLatency === 153)
    assert(ks.getWriteCount === 99)
    assert(ks.getWriteLatency === 36)
    assert(ks.getPendingFlushes === 44)
  }


  test("test split tables") {
    val tables = """		Table: cleanup
                 |		SSTable count: 0
                 |		Space used (live): 0
                 |		Space used (total): 0
                 |		Space used by snapshots (total): 0
                 |		Off heap memory used (total): 0
                 |		SSTable Compression Ratio: -1.0
                 |		Number of keys (estimate): 0
                 |		Memtable cell count: 0
                 |		Memtable data size: 0
                 |		Memtable off heap memory used: 0
                 |		Memtable switch count: 0
                 |		Local read count: 0
                 |		Local read latency: NaN ms
                 |		Local write count: 0
                 |		Local write latency: NaN ms
                 |		Pending flushes: 0
                 |		Percent repaired: 100.0
                 |		Bloom filter false positives: 0
                 |		Bloom filter false ratio: 0.00000
                 |		Bloom filter space used: 0
                 |		Bloom filter off heap memory used: 0
                 |		Index summary off heap memory used: 0
                 |		Compression metadata off heap memory used: 0
                 |		Compacted partition minimum bytes: 0
                 |		Compacted partition maximum bytes: 0
                 |		Compacted partition mean bytes: 0
                 |		Average live cells per slice (last five minutes): NaN
                 |		Maximum live cells per slice (last five minutes): 0
                 |		Average tombstones per slice (last five minutes): NaN
                 |		Maximum tombstones per slice (last five minutes): 0
                 |		Dropped Mutations: 0
                 |
                 |		Table (index): inode.cfs_archive_parent_pathinode.cfs_archive_parent_path
                 |		SSTable count: 0
                 |		Space used (live): 0
                 |		Space used (total): 0
                 |		Space used by snapshots (total): 0
                 |		Off heap memory used (total): 0
                 |		SSTable Compression Ratio: -1.0
                 |		Number of keys (estimate): 0
                 |		Memtable cell count: 0
                 |		Memtable data size: 0
                 |		Memtable off heap memory used: 0
                 |		Memtable switch count: 0
                 |		Local read count: 0
                 |		Local read latency: NaN ms
                 |		Local write count: 0
                 |		Local write latency: NaN ms
                 |		Pending flushes: 0
                 |		Percent repaired: 100.0
                 |		Bloom filter false positives: 0
                 |		Bloom filter false ratio: 0.00000
                 |		Bloom filter space used: 0
                 |		Bloom filter off heap memory used: 0
                 |		Index summary off heap memory used: 0
                 |		Compression metadata off heap memory used: 0
                 |		Compacted partition minimum bytes: 0
                 |		Compacted partition maximum bytes: 0
                 |		Compacted partition mean bytes: 0
                 |		Average live cells per slice (last five minutes): NaN
                 |		Maximum live cells per slice (last five minutes): 0
                 |		Average tombstones per slice (last five minutes): NaN
                 |		Maximum tombstones per slice (last five minutes): 0
                 |		Dropped Mutations: 0
                 |
                 |""".stripMargin
    val tbls = plugin.splitTables(tables)
    assert(tbls.length === 2)
  }

}
