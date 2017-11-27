package service

import java.io.File
import java.util.UUID

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.inject.ImplementedBy
import play.api.libs.Files
import service.log.LogPlugin

/**
  * Created by quentin on 07/06/17.
  */
//@ImplementedBy(classOf[LogAnalyzer])
trait DSECheckPlugin {
  def process(uuid: String, tmpPath:String)

  def forEachNode(tmp: String, applyOnEachNode: (String, UUID) => Unit) = {
    val folder = new File(tmp)
    folder.listFiles().toList.filter(_.isDirectory).foreach(folder => {
      applyOnEachNode(folder.getAbsolutePath, UUID.fromString(folder.getName))
    })
  }

  def readNodetoolInfo(tmpPath: String, nodeId: UUID): Iterator[String] ={
    scala.io.Source.fromFile(s"$tmpPath/$nodeId/info").getLines()
  }

  def readTablestat(tmpPath: String, nodeId: UUID): String ={
    scala.io.Source.fromFile(s"$tmpPath/$nodeId/cfstat").mkString
  }

  def readNodetoolStatus(tmpPath: String, nodeId: UUID) ={
    scala.io.Source.fromFile(s"$tmpPath/$nodeId/status").getLines()
  }

  def readCassandraYaml(tmpPath: String, nodeId: UUID): JsonNode ={
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.readTree(scala.io.Source.fromFile(s"$tmpPath/$nodeId/cassandra.yaml").mkString)
  }


}
