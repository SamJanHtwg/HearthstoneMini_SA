package hearthstoneMini
package model.cardareaComponent
import play.api.libs.json.JsValue

import scala.xml.Node
import hearthstoneMini.model.cardComponent.CardInterface

trait CardAreaInterface {
  val row: Vector[Option[CardInterface]]
  val size: Int
  
  def slot(slotNum: Int): Option[CardInterface]
  def replaceSlot(slotNum: Int, slot: Option[CardInterface]): CardAreaInterface
  def reduceDefVal(slotNum: Int, amount: Int): CardAreaInterface
  def reduceAttackCount(slotNum: Int): CardAreaInterface
  def resetAttackCount(): CardAreaInterface
  def toJson: JsValue
  def toXML: Node
}

