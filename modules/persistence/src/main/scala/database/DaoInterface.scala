package persistence
package database

import play.api.libs.json.JsValue

trait DaoInterface {
// TODO: use Field over JsValue
  def save(json: JsValue): Unit
  def load(): JsValue
  def update(): Unit
//   def delete(): Unit
}
