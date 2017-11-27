package service

import scala.util.control.NonFatal

object ParsingUtils {

//  import java.text.DecimalFormat
//  val tl = new ThreadLocal[DecimalFormat]
//  tl.set(new DecimalFormat("00000"))


  def toJavaInteger(txt: String): Integer = {
    try{
      txt.replaceAll("\\s", "").toInt
    } catch {
      case NonFatal(e) => null
    }
  }

  def toJavaLong(txt: String): java.lang.Long = {
    try{
      txt.replaceAll("\\s", "").toLong
    } catch {
      case NonFatal(e) => null
    }
  }

  def toJavaFloat(txt: String): java.lang.Float = {
    try{
      txt.replaceAll("\\s", "").replaceAll(",", ".").toFloat
    } catch {
      case NonFatal(e) => null
    }
  }

}
