package persistence.fileIO

import scala.util.Try
import play.api.libs.json.JsValue

trait FileIOInterface {
  def load(): Try[JsValue]
  def save(json: JsValue): Unit
}
