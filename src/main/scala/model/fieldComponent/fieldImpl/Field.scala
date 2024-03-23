package hearthstoneMini
package model.fieldComponent.fieldImpl

import model.fieldComponent.FieldInterface
import model.playerComponent.playerImpl.Player
import model.matrixComponent.matrixImpl.Matrix
import play.api.libs.json.*

import javax.inject.Inject
import scala.xml.Node

object FieldObject {
  val offset: Int = 1
  val standartSlotNum: Int = 5
  val standartCardWidth: Int = 15
  val standartCardHeight: Int = 5
  val standartSlotWidth: Int = standartCardWidth + 2 // 2 for Margin
  val standartFieldBarHeight: Int = standartCardHeight + 1 // + 2 for border
  val standartGameBarHeight: Int = 7
  val standartMenueBarHeight: Int = 2
  val standartFieldWidth: Int = standartSlotNum * standartSlotWidth
  val standartFieldHeight: Int = (standartFieldBarHeight + standartGameBarHeight + standartMenueBarHeight) * 2
    + FieldObject.offset

  def fromJson(json: JsValue): Field = {
    val fieldJs = json \ "field"
    Field(
      players = (fieldJs \ "players").validate[List[JsValue]].get.map(player => Player.fromJson(player)),
      turns = (fieldJs \ "turns").get.toString.toInt,
      slotNum = (fieldJs \ "slotnum").get.toString.toInt
    )
  }

  def fromXML(node: Node): Field = Field(
    players = (node \\ "players" \ "entry").map(player => Player.fromXML(player)).toList,
    turns = (node \ "turns").head.text.toInt,
    slotNum = (node \ "slotnum").head.text.toInt
  )
}

case class Field @Inject()(matrix: Matrix[String] = new Matrix[String](FieldObject.standartFieldHeight,
  FieldObject.standartFieldWidth, " "),
                           slotNum: Int = FieldObject.standartSlotNum,
                           players: List[Player] = List[Player](Player(id = 1), Player(id = 2)),
                           turns: Int = 0) extends FieldInterface() {
  // The active player is the Player in position 0 of the list

  def this(size: Int, player1: String, player2: String) = this(new Matrix[String](FieldObject.standartFieldHeight,
    FieldObject.standartSlotWidth * size, " "),
    size,
    players = List[Player](Player(name = player1, id = 1), Player(name = player2, id = 2)))

  def this(size: Int) = this(new Matrix[String](FieldObject.standartFieldHeight, FieldObject.standartSlotWidth * size,
    " "),
    size,
    players = List[Player](Player(id = 1), Player(id = 2)))

  override def placeCard(handSlot: Int, fieldSlot: Int): Field =
    copy(players = players.updated(0, players.head.placeCard(handSlot, fieldSlot).reduceMana(players.head.gamebar
      .hand(handSlot).manaCost)))

  override def drawCard(): Field = copy(players = players.updated(0, players.head.drawCard()))

  override def destroyCard(player: Int, slot: Int): Field = copy(players = players.updated(player, players(player)
    .destroyCard(slot)))

  override def reduceHp(player: Int, amount: Int): Field = copy(players = players.updated(player, players(player)
    .reduceHp(amount)))

  override def increaseHp(amount: Int): Field = copy(players = players.updated(0, players.head.increaseHp(amount)))

  override def reduceMana(amount: Int): Field = copy(players = players.updated(0, players.head.reduceMana(amount)))

  override def increaseMana(amount: Int): Field = copy(players = players.updated(0, players.head.increaseMana(amount)))

  override def reduceAttackCount(slotNum: Int): Field = copy(players = players.updated(0,
    players.head.reduceAttackCount(slotNum)))

  override def resetAttackCount(): Field = copy(
    players = players.updated(0, players.head.resetAttackCount()).updated(1, players(1).resetAttackCount()))

  override def resetAndIncreaseMana(): Field = copy(players = players.updated(0, players.head.resetAndIncreaseMana())
    .updated(1, players(1).resetAndIncreaseMana()))

  override def setPlayerNames(p1: String, p2: String): Field = copy(players = players.updated(0, players.head
    .setName(p1)).updated(1, players(1).setName(p2)))

  override def setHpValues(amount: Int): Field = copy(players = players.updated(0, players.head.setHpValue(amount))
    .updated(1, players(1).setHpValue(amount)))

  override def setManaValues(amount: Int): Field = copy(players = players.updated(0, players.head.setManaValue(amount))
    .updated(1, players(1).setManaValue(amount)))

  override def switchPlayer(): Field = if (turns != 0 && turns % 2 == 1)
  then copy(players = players.updated(0, players.head.resetAndIncreaseMana()).updated(1, players(1)
      .resetAndIncreaseMana()).reverse, turns = turns + 1)
  else copy(players = players.reverse, turns = turns + 1)

  override def getPlayerById(id: Int): Player = players.filter(_.id == id).head

  override def getActivePlayer: Player = players.head

  override def reduceDefVal(slotNum: Int, amount: Int): Field = copy(
    players = players.updated(1, players(1).reduceDefVal(slotNum, amount)))

  override def toMatrix: Matrix[String] = matrix
    .updateMatrix(0, 0, List[String]("-" * FieldObject.standartFieldWidth))
    .updateMatrixWithMatrix(FieldObject.offset, 0, getPlayerById(1).toMatrix)
    .updateMatrixWithMatrix(FieldObject.offset + FieldObject.standartMenueBarHeight + FieldObject.standartGameBarHeight
      + FieldObject.standartFieldBarHeight, 0, getPlayerById(2).toMatrix)

  override def toString: String = toMatrix.rows.map(_.mkString("|", "", "|\n")).mkString

  override def toJson: JsValue = Json.obj(
    "players" -> players.map(player => player.toJson),
    "slotnum" -> Json.toJson(slotNum),
    "turns" -> Json.toJson(turns)
  )

  override def toXML: Node =
    <Field>
      <players>
        {players.map(player => <entry>
        {player.toXML}
      </entry>)}
      </players>
      <slotnum>
        {slotNum.toString}
      </slotnum>
      <turns>
        {turns.toString}
      </turns>
    </Field>
}
