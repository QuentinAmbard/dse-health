package service.topology

import java.util.UUID

import org.scalamock.scalatest.MockFactory

/**
  * Created by quentin on 21/11/17.
  */
class TopologyPluginTest extends org.scalatest.FunSuite with MockFactory {

  val nodetoolStr =
    """Datacenter: DC1
      |==============
      |Status=Up/Down
      ||/ State=Normal/Leaving/Joining/Moving
      |-- Address Load Tokens Owns (effective) Host ID Rack
      |UN 123.11.37.154 31,03 GB 256 61,4% 903670b4-7f43-481a-a8f6-073d7eeca3af RAC1
      |UN 123.11.37.155 29,91 GB 256 61,0% b4d29185-7fa6-4aa2-8f50-d7c2441fa06f RAC1
      |UN 123.11.37.152 30,26 GB 256 60,0% c0d11f8f-4985-4fc0-a358-241047282c1d RAC1
      |UN 123.11.37.153 30,28 GB 256 60,5% c57293d6-ec58-4042-8ec3-24b0a2c2941c RAC1
      |UN 123.11.37.151 28,69 GB 256 57,1% 881c4880-94a2-45f9-b73c-39b44c683af3 RAC1
      |Datacenter: MY-DC2
      |===============
      |Status=Up/Down
      ||/ State=Normal/Leaving/Joining/Moving
      |-- Address Load Tokens Owns (effective) Host ID Rack
      |UN 11.1.30.11 21,89 GB 256 0,0% e5b5136b-d564-4bf3-8ff7-6fc99f7221c3 RACK2
      |DN 11.1.30.12 21,56 GB 256 0,0% 4c4da95a-601f-41ae-94ff-3c728411b480 RACK2
      |UJ 11.1.30.31 45,31 GB 256 0,0% 4d862177-6b6e-496d-9074-331b942e7332 RACK3
      |UL 11.1.30.21 22,17 GB 256 0,0% 9e2fa7f3-0eb7-4fbc-ada2-6a36bd4b22b1 RACK3
      |UN 11.1.30.22 11,05 GB 256 0,0% 863d86c4-3146-4142-918a-d1167e459e8c RACK4""".stripMargin

  val nRepo = mock[NodeRepository]
  val nsRepo = mock[NodeStatusRepository]
  val plugin = new TopologyPlugin(nRepo, nsRepo)

  test("test split DC") {
    val dcStrs = plugin.splitDC(nodetoolStr)
    dcStrs.length === 2
  }

  test("test extract node") {
    val nodeStr =
      """Datacenter: MY-DC2
        |===============
        |Status=Up/Down
        ||/ State=Normal/Leaving/Joining/Moving
        |-- Address Load Tokens Owns (effective) Host ID Rack
        |UN 11.1.30.11 21,89 GB 256 0,0% e5b5136b-d564-4bf3-8ff7-6fc99f7221c3 RACK2
        |DN 11.1.30.12 21,56 GB 256 0,0% 4c4da95a-601f-41ae-94ff-3c728411b480 RACK2
        |UJ 11.1.30.31 45,31 GB 256 0,0% 4d862177-6b6e-496d-9074-331b942e7332 RACK3
        |UL 11.1.30.21 22,17 GB 256 0,0% 9e2fa7f3-0eb7-4fbc-ada2-6a36bd4b22b1 RACK3
        |UN 11.1.30.22 11,05 GB 256 0,0% 863d86c4-3146-4142-918a-d1167e459e8c RACK4""".stripMargin
    val nodeId = UUID.randomUUID()
    val nodes = plugin.extractNodeStatus("reportId", nodeId, "myDC", nodeStr).toList
    assert(nodes.size == 5)
    assert(nodes(0).getStatus === "U")
    assert(nodes(0).getState === "N")
    assert(nodes(0).getId === nodeId)
    assert(nodes(1).getTargetIp === "11.1.30.12")
    assert(nodes(1).getLoad === 21.56F)
    assert(nodes(1).getEffective === 0.0F)
    assert(nodes(1).getTargetId === UUID.fromString("4c4da95a-601f-41ae-94ff-3c728411b480"))
    assert(nodes(1).getId === nodeId)
    assert(nodes(2).getToken === 256)
    assert(nodes(2).getRack === "RACK3")
    assert(nodes(2).getDc === "myDC")
    assert(nodes(2).getStatus === "U")
    assert(nodes(2).getState === "J")
    assert(nodes(3).getStatus === "U")
    assert(nodes(3).getState === "L")
  }

  test("extractNodeFromInfo") {
    val nodetoolInfo =
      """ID                     : 4b49601b-7804-44f5-adb5-986abe25aa88
        |Gossip active          : true
        |Thrift active          : true
        |Native Transport active: true
        |Load                   : 467.26 MiB
        |Generation No          : 1510927401
        |Uptime (seconds)       : 118740
        |Heap Memory (MB)       : 5189.74 / 7942.00
        |Off Heap Memory (MB)   : 2.20
        |Data Center            : dc1
        |Rack                   : rack1
        |Exceptions             : 33
        |Key Cache              : entries 255, size 180 KiB, capacity 100 MiB, 17089026 hits, 17153961 requests, 0.996 recent hit rate, 14400 save period in seconds
        |Row Cache              : entries 0, size 0 bytes, capacity 0 bytes, 0 hits, 0 requests, NaN recent hit rate, 0 save period in seconds
        |Counter Cache          : entries 0, size 0 bytes, capacity 50 MiB, 0 hits, 0 requests, NaN recent hit rate, 7200 save period in seconds
        |Chunk Cache            : entries 4915, size 307.19 MiB, capacity 480 MiB, 235794 misses, 8809205 requests, 0.973 recent hit rate, NaN microseconds miss latency
        |Percent Repaired       : 98.12%
        |Token                  : 6561882446563195210
        |""".stripMargin
    val nodeId = UUID.fromString("4b49601b-7804-44f5-adb5-986abe25aa88")
    val node = plugin.extractNodeFromInfo("reportId", nodeId, nodetoolInfo.lines)
    assert(node.getReportId === "reportId")
    assert(node.getId === nodeId)
    assert(node.getDc === "dc1")
    assert(node.getRack === "rack1")
    assert(node.getPercentRepaired === 98.12F)
    assert(node.getException === 33)
  }

}
