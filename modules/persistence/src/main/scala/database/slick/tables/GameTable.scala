package persistence
package database.slick.tables

import _root_.persistence.database.slick.MyPostgresProfile.MyAPI.playJsonTypeMapper
import _root_.persistence.database.slick.MyPostgresProfile.api.*
import play.api.libs.json.JsValue
import slick.jdbc.*

class TestTable(tag: Tag) extends Table[(String, JsValue)](tag, "test") {
  def key = column[String]("key", O.PrimaryKey)
  def value = column[JsValue]("value")

  def * = (key, value)
}
