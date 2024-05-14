package persistence
package database

import play.api.libs.json.JsValue
import model.fieldComponent.FieldInterface
import scala.util.Try

trait DaoInterface {
// TODO: use Field over JsValue
  def save(field: FieldInterface): Unit
  def load(): Try[JsValue]
  def update(field: FieldInterface): Unit
//   def delete(): Unit
}
