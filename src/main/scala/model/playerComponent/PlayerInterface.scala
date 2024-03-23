package hearthstoneMini
package model.playerComponent
import model.matrixComponent.matrixImpl.Matrix
import play.api.libs.json.JsValue

import scala.xml.Node
import hearthstoneMini.model.gamebarComponent.GamebarInterface
import scala.compiletime.ops.int
import hearthstoneMini.model.fieldbarComponent.FieldbarInterface
import hearthstoneMini.model.cardareaComponent.CardAreaInterface

trait PlayerInterface {
  val id: Int
  val name: String
  val gamebar: GamebarInterface
  val fieldbar: FieldbarInterface
  
  def placeCard(handSlot: Int, fieldSlot: Int ): PlayerInterface
  def drawCard(): PlayerInterface
  def destroyCard(fieldSlot: Int): PlayerInterface
  def reduceHp(amount: Int): PlayerInterface
  def increaseHp(amount: Int): PlayerInterface
  def reduceMana(amount: Int): PlayerInterface
  def increaseMana(amount: Int): PlayerInterface
  def resetAndIncreaseMana(): PlayerInterface
  def setName(name: String): PlayerInterface
  def setHpValue(amount: Int): PlayerInterface
  def setManaValue(amount: Int): PlayerInterface
  def toMatrix: Matrix[String]
  def reduceAttackCount(slotNum: Int): PlayerInterface
  def reduceDefVal(slotNum: Int, amount: Int): PlayerInterface
  def resetAttackCount(): PlayerInterface
  def renderUnevenId(): Matrix[String]
  def renderEvenId(): Matrix[String]
  def menueBar(): Matrix[String]
  def toJson: JsValue
  def toXML: Node
}
