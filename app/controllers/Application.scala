package controllers

import java.io.IOException
import java.nio.file.Path
import javax.inject.Inject

import org.apache.commons.io.FileUtils
import play.api._
import play.api.libs.Files
import play.api.mvc._
import service.{DSECheckPlugin, Unzipper}

import scala.util.Random
import collection.JavaConverters._

class Application @Inject () (plugins: java.util.Set[DSECheckPlugin]) extends Controller with Unzipper{
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def upload = Action(parse.multipartFormData) { request =>

    request.body.file("analysis").map { analysis =>
      import java.io.File
      val filename = analysis.filename
      val reportId = Random.alphanumeric.take(20).mkString("")
      val tmpFolder = s"/tmp/dse-health/$reportId"
      try {
        FileUtils.forceMkdir(new File(tmpFolder))
        untarTo(analysis.ref.file, tmpFolder)
        plugins.asScala.foreach(_.process(reportId, tmpFolder))
      } finally {
        try {
          FileUtils.deleteDirectory(new File(tmpFolder))
        }catch {
          case e: IOException => println("error todo à logger")
        }
      }
      Ok("File uploaded")
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file")
    }
  }
}
