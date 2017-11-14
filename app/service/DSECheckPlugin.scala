package service

import java.io.File

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

  def forEachNode(tmp: String, applyOnEachNode: (String, String) => Unit) = {
    val folder = new File(tmp)
    folder.listFiles().toList.filter(_.isDirectory).foreach(folder => {
      applyOnEachNode(folder.getAbsolutePath, folder.getName)
    })
  }

  def readNodetoolInfo(tmpPath: String, nodeId: String): Iterator[String] ={
    scala.io.Source.fromFile(s"$tmpPath/$nodeId/info").getLines()
  }

  def readTablestat(tmpPath: String, nodeId: String): String ={
    scala.io.Source.fromFile(s"$tmpPath/$nodeId/cfstat").mkString
  }


  def readCassandraYaml(tmpPath: String, nodeId: String): JsonNode ={
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.readTree(scala.io.Source.fromFile(s"$tmpPath/$nodeId/cassandra.yaml").mkString)
  }


}
