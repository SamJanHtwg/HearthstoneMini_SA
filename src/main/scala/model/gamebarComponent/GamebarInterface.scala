package hearthstoneMini
package model.gamebarComponent

import model.cardComponent.cardImpl.Card
import model.matrixComponent.matrixImpl.Matrix
import model.fieldComponent.fieldImpl.FieldObject
import model.gamebarComponent.gamebarImpl.Gamebar
import play.api.libs.json.JsValue

import scala.xml.Node
import hearthstoneMini.model.healthpointsComponent.HealthpointsInterface
import hearthstoneMini.model.cardareaComponent.CardAreaInterface
import hearthstoneMini.model.cardComponent.CardInterface
import hearthstoneMini.model.manaComponent.ManaInterface

trait GamebarInterface {
  val hp: HealthpointsInterface
  val mana: ManaInterface
  val hand: List[CardInterface]
  val deck: List[CardInterface]
  val friedhof: Array[CardInterface]

  def removeCardFromHand(slot: Int): GamebarInterface
  def addCardToHand(card: Option[CardInterface]): GamebarInterface
  def addCardToFriedhof(card: Option[CardInterface]): GamebarInterface
  def reduceHp(amount: Int): GamebarInterface
  def increaseHp(amount: Int): GamebarInterface
  def reduceMana(amount: Int): GamebarInterface
  def increaseMana(amount: Int): GamebarInterface
  def resetAndIncreaseMana(): GamebarInterface
  def drawCard(): GamebarInterface
  def setManaValue(amount: Int): GamebarInterface
  def setHpValue(amount: Int): GamebarInterface
  def handAsMatrix(): Matrix[String]
  def toMatrix: Matrix[String]
  def toJson: JsValue
  def toXML: Node
}
