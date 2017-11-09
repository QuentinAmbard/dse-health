package service.datamodel.topology

import org.scalamock.scalatest.MockFactory

/**
  * Created by quentin on 16/07/17.
  */
class DataModelPluginTest extends org.scalatest.FunSuite with MockFactory {
  val lines = scala.io.Source.fromInputStream(getClass.getResourceAsStream("cfstat")).mkString
  val repo = mock[CassandraTableRepository]
  val plugin = new DataModelPlugin(repo)

  //  test("parse tables from cfstats.") {
  //    println(lines)
  //    //val session = mock[CassandraSession]
  //    val repo = mock[CassandraTableRepository]
  //    val plugin = new DataModelPlugin(repo)
  //    val tables = plugin.extractCassandraTable(lines)
  //    tables.foreach(println(_))
  //
  //  }

  test("read table stats.") {
    val tbstats =
      """		Table: available_ranges
        |		SSTable count: 1
        |		Space used (live): 2
        |		Space used (total): 3
        |		Space used by snapshots (total): 4
        |		Off heap memory used (total): 5
        |		SSTable Compression Ratio: -1.0
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
        |		Percent repaired: 16.0
        |		Bloom filter false positives: 17
        |		Bloom filter false ratio: 0.00000
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
        |		Maximum tombstones per slice (last five minutes): 27
        |		Dropped Mutations: 29""".stripMargin
    val table = plugin.extractTable(tbstats)
    assert(table.getName === "available_ranges")
    assert("test a terminer" === "ho oui des regex!")
  }


  test("split keyspaces from cfstats.") {
    val ks = plugin.splitKeyspaces(lines)
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
    val (ks, b) = plugin.extractKeyspaceData(test)
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
    val (ks, b) = plugin.extractKeyspaceData(test)
    assert(b === beginingTable)
    assert(ks.getName === "test")
    assert(ks.getReadCount === 12)
    assert(ks.getReadLatency === 153)
    assert(ks.getWriteCount === 99)
    assert(ks.getWriteLatency === 36)
    assert(ks.getPendingFlushes === 44)
  }

}
