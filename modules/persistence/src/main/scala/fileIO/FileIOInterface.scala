package persistence.fileIO

import model.fieldComponent.FieldInterface
import scala.util.Try
import play.api.libs.json.JsValue

trait FileIOInterface {
  def load(): Try[FieldInterface]
  def save(field: FieldInterface): Unit
  def save(json: JsValue): Unit
}
