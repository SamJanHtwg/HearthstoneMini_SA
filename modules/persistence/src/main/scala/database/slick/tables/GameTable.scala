package persistence
package database.slick.tables

import _root_.persistence.database.slick.MyPostgresProfile.MyAPI.playJsonTypeMapper
import _root_.persistence.database.slick.MyPostgresProfile.MyAPI.playJsonArrayTypeMapper
import _root_.persistence.database.slick.MyPostgresProfile.MyAPI.playJsonArrayOptionTypeMapper
import _root_.persistence.database.slick.MyPostgresProfile.api.*
import play.api.libs.json.JsValue
import slick.jdbc.*

class GameTable(tag: Tag) extends Table[(String, String, String, Int, String, Int)](tag, "game") {
  def key = column[String]("key", O.PrimaryKey)
  def player1 = column[String]("player1")
  def player2 = column[String]("player2")
  def turns = column[Int]("turns")
  def gameState = column[String]("gameState")
  def activePlayerId = column[Int]("activePlayerId")

  def * = (key, player1, player2, turns, gameState, activePlayerId)
  def player1_fk = foreignKey("player1_fk", player1, TableQuery[PlayerTable])(_.key)
  def player2_fk = foreignKey("player2_fk", player2, TableQuery[PlayerTable])(_.key)
}

class PlayerTable(tag: Tag) extends Table[(String, Int , List[JsValue], List[JsValue], String, List[Option[JsValue]], Int, List[JsValue], Int, Int, Int)](tag, "player") {
  def key = column[String]("key", O.PrimaryKey)
  def id = column[Int]("id")
  def deck = column[List[JsValue]]("deck")
  def hand = column[List[JsValue]]("hand")
  def name = column[String]("name")
  def field = column[List[Option[JsValue]]]("field")
  def hpValue = column[Int]("hpValue")
  def friedhof = column[List[JsValue]]("friedhof")
  def manaValue = column[Int]("manaValue")
  def maxHpValue = column[Int]("maxHpValue")
  def maxManaValue = column[Int]("maxManaValue")

  def * = (key, id, deck, hand, name, field, hpValue, friedhof, manaValue, maxHpValue, maxManaValue)
}
