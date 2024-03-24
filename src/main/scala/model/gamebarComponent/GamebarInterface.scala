package hearthstoneMini
package model.gamebarComponent

import model.cardComponent.cardImpl.Card
import model.matrixComponent.matrixImpl.Matrix
import model.fieldComponent.fieldImpl.FieldObject
import model.gamebarComponent.gamebarImpl.Gamebar
import play.api.libs.json.JsValue

import scala.xml.Node
import hearthstoneMini.model.cardareaComponent.CardAreaInterface
import hearthstoneMini.model.cardComponent.CardInterface

trait GamebarInterface {
  val hand: List[CardInterface]
  val deck: List[CardInterface]
  val friedhof: Array[CardInterface]

  def removeCardFromHand(slot: Int): GamebarInterface

  def addCardToHand(card: Option[CardInterface]): GamebarInterface

  def addCardToFriedhof(card: Option[CardInterface]): GamebarInterface

  def drawCard(): GamebarInterface
  
  def handAsMatrix(): Matrix[String]

  def toMatrix: Matrix[String]

  def toJson: JsValue

  def toXML: Node
}
