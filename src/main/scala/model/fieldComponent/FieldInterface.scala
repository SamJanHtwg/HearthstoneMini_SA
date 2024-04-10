package hearthstoneMini
package model.fieldComponent

import model.cardComponent.cardImpl.Card
import model.playerComponent.playerImpl.Player
import model.matrixComponent.matrixImpl.Matrix
import model.fieldComponent
import play.api.libs.json.JsValue

import scala.xml.Node
import hearthstoneMini.model.playerComponent.PlayerInterface

trait FieldInterface {
  val players: Map[Int, PlayerInterface]
  val activePlayerId: Int
  val slotNum: Int
  val turns: Int

  // player
  def placeCard(handSlot: Int, fieldSlot: Int): FieldInterface

  def getInactivePlayerId: Int

  def drawCard(): FieldInterface

  def destroyCard(player: Int, slot: Int): FieldInterface

  def setPlayerNames(p1: String, p2: String): FieldInterface

  def reduceAttackCount(slotNum: Int): FieldInterface

  def resetAttackCount(): FieldInterface

  def reduceDefVal(slotNum: Int, amount: Int): FieldInterface

  def switchPlayer(): FieldInterface

  def getPlayerById(id: Int): Player

  def getActivePlayer: Player

  // hp
  def reduceHp(player: Int, amount: Int): FieldInterface

  def increaseHp(amount: Int): FieldInterface

  def setHpValues(amount: Int): FieldInterface

  // mana
  def reduceMana(amount: Int): FieldInterface

  def increaseMana(amount: Int): FieldInterface

  def resetAndIncreaseMana(): FieldInterface
  def setManaValues(amount: Int): FieldInterface

  def toJson: JsValue

  def toXML: Node

}
