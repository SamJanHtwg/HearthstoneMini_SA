package core
package model.cardComponent

import model.cardComponent.cardImpl.Card
import model.fieldComponent.fieldImpl.Field
import play.api.libs.json.JsValue
import scala.xml.Node

trait CardInterface {
  val name: String
  val manaCost: Int
  val attValue: Int
  val defenseValue: Int
  val id: String
  val effect: String
  val rarity: String
  val attackCount: Int

  def reduceHP(amount: Int): CardInterface
  def reduceAttackCount(): CardInterface
  def resetAttackCount(): CardInterface

  def toJson: JsValue
  def toXML: Node
}
