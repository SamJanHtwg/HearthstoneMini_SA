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

class PlayerTable(tag: Tag) extends Table[(String, String, JsValue, String, JsValue, Int, String, Int, Int, Int)](tag, "player") {
  def id = column[String]("id", O.PrimaryKey)
  def deck = column[String]("deck")
  def hand = column[JsValue]("hand")
  def name = column[String]("name")
  def field = column[JsValue]("field")
  def hpValue = column[Int]("hpValue")
  def friedhof = column[String]("friedhof")
  def manaValue = column[Int]("manaValue")
  def maxHpValue = column[Int]("maxHpValue")
  def maxManaValue = column[Int]("maxManaValue")

  def * = (id, deck, hand, name, field, hpValue, friedhof, manaValue, maxHpValue, maxManaValue)
}
