package model
package fileIOComponent.jsonIOImpl
import fieldComponent.FieldInterface
import fieldComponent.fieldImpl.{Field}
import fileIOComponent.FileIOInterface
import play.api.libs.json.*

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.Try

class FileIO extends FileIOInterface {
  override def load: Try[Field] = Try {
    val source = Source.fromFile("field.json")
    val json = Json.parse(source.getLines().mkString)
    source.close()
    Field.fromJson(json)
  }

  override def save(field: FieldInterface): Unit = {
    val pw = new PrintWriter(new File("field.json"))
    val save = Json.obj(
      "field" -> field.toJson
    )
    pw.write(Json.prettyPrint(save))
    pw.close()
  }
}
