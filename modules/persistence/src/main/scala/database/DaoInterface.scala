package persistence.database

import play.api.libs.json.JsValue
import model.fieldComponent.FieldInterface
import scala.util.Try

trait DaoInterface {
  def save(field: FieldInterface): Unit
  def load(): Try[JsValue]
  def update(field: FieldInterface): Unit
  def delete(): Try[Unit]
}
