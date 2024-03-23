package hearthstoneMini
package model.manaComponent

import play.api.libs.json.JsValue
import scala.xml.Node

trait ManaInterface {
  val value: Int
  val max: Int
  def increase(amount: Int): ManaInterface 
  def resetAndIncrease(): ManaInterface 
  def decrease(amount: Int): ManaInterface 
  def isEmpty: Boolean
  def setVal(amount: Int): ManaInterface
  def toJson: JsValue
  def toXML: Node
}

