package hearthstoneMini
package model.playerComponent

import model.matrixComponent.matrixImpl.Matrix
import play.api.libs.json.JsValue

import scala.xml.Node
import scala.compiletime.ops.int
import hearthstoneMini.model.fieldbarComponent.FieldbarInterface
import hearthstoneMini.model.cardareaComponent.CardAreaInterface
import hearthstoneMini.model.cardComponent.CardInterface

trait PlayerInterface {
  val id: Int
  val name: String
  val hand: List[CardInterface]
  val deck: List[CardInterface]
  val friedhof: Array[CardInterface]
  val hpValue: Int
  val maxHpValue: Int
  val manaValue: Int
  val maxManaValue: Int
  val fieldbar: FieldbarInterface


  // player
  def placeCard(handSlot: Int, fieldSlot: Int): PlayerInterface

  def drawCard(): PlayerInterface

  def setName(name: String): PlayerInterface

  def reduceAttackCount(slotNum: Int): PlayerInterface

  def reduceDefVal(slotNum: Int, amount: Int): PlayerInterface

  def resetAttackCount(): PlayerInterface
  
  def destroyCard(fieldSlot: Int): PlayerInterface

  // hp
  def reduceHp(amount: Int): PlayerInterface

  def setHpValue(amount: Int): PlayerInterface
  
  def increaseHp(amount: Int): PlayerInterface
  
  def isHpEmpty: Boolean

  // mana
  def reduceMana(amount: Int): PlayerInterface
  
  def increaseMana(amount: Int): PlayerInterface

  def resetAndIncreaseMana(): PlayerInterface

  def setManaValue(amount: Int): PlayerInterface

  def isManaEmpty: Boolean

  // matrix
  def toMatrix: Matrix[String]

  def renderUnevenId(): Matrix[String]

  def renderEvenId(): Matrix[String]

  def menueBar(): Matrix[String]
  

  def toJson: JsValue

  def toXML: Node
}
