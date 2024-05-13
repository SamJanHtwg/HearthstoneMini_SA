package persistence
package database

import play.api.libs.json.JsValue
import model.fieldComponent.FieldInterface

trait DaoInterface {
// TODO: use Field over JsValue
  def save(field: FieldInterface): Unit
  def load(): JsValue
  def update(): Unit
//   def delete(): Unit
}
