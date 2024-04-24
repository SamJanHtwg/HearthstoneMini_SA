package persistence
package fileIO.jsonIOImpl

import fileIO.FileIOInterface
import play.api.libs.json.*

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.Try
import fileIO.FileIOInterface

class JsonIO extends FileIOInterface {
  override def load(): Try[JsValue] = Try {
    val source = Source.fromFile("field.json")
    val json = Json.parse(source.getLines().mkString)
    source.close()
    json
  }

  override def save(json: JsValue): Unit = {
    val pw = new PrintWriter(new File("field.json"))
    pw.write(Json.prettyPrint(json))
    pw.close()
  }
}
