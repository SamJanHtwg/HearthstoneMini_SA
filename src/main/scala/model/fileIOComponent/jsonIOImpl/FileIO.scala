package hearthstoneMini
package model.fileIOComponent.jsonIOImpl
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.{Field, FieldObject}
import model.fileIOComponent.FileIOInterface
import play.api.libs.json.*

import java.io.{File, PrintWriter}
import scala.io.Source

class FileIO extends FileIOInterface {
  override def load: Field = {
    val source = Source.fromFile("field.json")
    val json = Json.parse( source.getLines().mkString )
    source.close()
    FieldObject.fromJson(json)
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
