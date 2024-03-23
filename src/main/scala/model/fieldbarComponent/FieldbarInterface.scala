package hearthstoneMini
package model.fieldbarComponent

import model.matrixComponent.matrixImpl.Matrix
import play.api.libs.json.JsValue

import scala.xml.Node
import hearthstoneMini.model.cardareaComponent.CardAreaInterface
import hearthstoneMini.model.cardComponent.CardInterface
import hearthstoneMini.model.matrixComponent.MatrixInterface

trait FieldbarInterface {
  val size: Int
  val cardArea: CardAreaInterface
  val matrix: MatrixInterface
  
  def placeCard(slot: Int, card: CardInterface): FieldbarInterface
  def removeCard(slot: Int): FieldbarInterface
  def toMatrix: Matrix[String]
  def reduceDefVal(slotNum: Int, amount: Int): FieldbarInterface
  def reduceAttackCount(slotNum: Int): FieldbarInterface
  def resetAttackCount(): FieldbarInterface
  def toJson: JsValue
  def toXML: Node
}
