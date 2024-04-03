package hearthstoneMini
package model.fieldComponent.fieldImpl

import model.fieldComponent.FieldInterface
import model.playerComponent.playerImpl.Player
import model.matrixComponent.matrixImpl.Matrix
import play.api.libs.json.*

import javax.inject.Inject
import scala.xml.Node
import scalafx.scene.input.KeyCode.M

/** Serialisierbarkeit players
  */
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
  val standartFieldHeight: Int =
    (standartFieldBarHeight + standartGameBarHeight + standartMenueBarHeight) * 2
      + FieldObject.offset

  def fromJson(json: JsValue): Field = {
    val fieldJs = json \ "field"
    Field(
      activePlayerId = (fieldJs \ "activePlayerId").get.toString.toInt,
      players = (fieldJs \ "players").validate[Iterable[JsValue]].get.map(player => Player.fromJson(player)).map(player => (player.id, player)).toMap,
      turns = (fieldJs \ "turns").get.toString.toInt,
      slotNum = (fieldJs \ "slotnum").get.toString.toInt
    )
  }

  def fromXML(node: Node): Field = Field(
    activePlayerId = (node \ "activePlayerId").head.text.toInt,
    players = Map[Int, Player](),
    // players = (node \\ "players" \ "entry").map(player => Player.fromXML(player)).toMap[String, Player],
    turns = (node \ "turns").head.text.toInt,
    slotNum = (node \ "slotnum").head.text.toInt
  )
}

//noinspection DuplicatedCode
case class Field @Inject() (
    matrix: Matrix[String] = new Matrix[String](
      FieldObject.standartFieldHeight,
      FieldObject.standartFieldWidth,
      " "
    ),
    slotNum: Int = FieldObject.standartSlotNum,
    players: Map[Int, Player] = Map[Int, Player](),
    activePlayerId: Int = 1,
    turns: Int = 0
) extends FieldInterface() {

  def this(size: Int, player1: String, player2: String) = this(
    new Matrix[String](
      FieldObject.standartFieldHeight,
      FieldObject.standartSlotWidth * size,
      " "
    ),
    size,
    players = Map[Int, Player](
      (1, Player(name = player1, id = 1, manaValue = 0, maxManaValue = 1)),
      (2, Player(name = player2, id = 2, manaValue = 0, maxManaValue = 1))
    ),
    activePlayerId = 1
  )

  def this(size: Int) = this(
    new Matrix[String](
      FieldObject.standartFieldHeight,
      FieldObject.standartSlotWidth * size,
      " "
    ),
    size,
    activePlayerId = 1,
    players = Map[Int, Player](
      (1, Player(id = 1, manaValue = 0, maxManaValue = 1)),
      (2, Player(id = 2, manaValue = 0, maxManaValue = 1))
    )
  )

  override def placeCard(handSlot: Int, fieldSlot: Int): Field =
    copy(players =
      players.updated(
        activePlayerId,
        players(activePlayerId)
          .placeCard(handSlot, fieldSlot)
          .reduceMana(players(activePlayerId).hand(handSlot).manaCost)
      )
    )

  override def drawCard(): Field = copy(players =
    players.updated(activePlayerId, players(activePlayerId).drawCard())
  )

  override def destroyCard(player: Int, slot: Int): Field = copy(players =
    players.updated(
      player,
      players(player)
        .destroyCard(slot)
    )
  )

  override def reduceHp(player: Int, amount: Int): Field = copy(players =
    players.updated(
      player,
      players(player)
        .reduceHp(amount)
    )
  )

  override def increaseHp(amount: Int): Field = copy(players =
    players.updated(activePlayerId, players(activePlayerId).increaseHp(amount))
  )

  override def reduceMana(amount: Int): Field = copy(players =
    players.updated(activePlayerId, players(activePlayerId).reduceMana(amount))
  )

  override def increaseMana(amount: Int): Field = copy(players =
    players.updated(
      activePlayerId,
      players(activePlayerId).increaseMana(amount)
    )
  )

  override def reduceAttackCount(slotNum: Int): Field = copy(players =
    players.updated(
      activePlayerId,
      players(activePlayerId).reduceAttackCount(slotNum)
    )
  )

  override def resetAttackCount(): Field = copy(
    players = players
      .updated(activePlayerId, players(activePlayerId).resetAttackCount())
      .updated(
        getInactivePlayerId,
        players(getInactivePlayerId).resetAttackCount()
      )
  )

  override def resetAndIncreaseMana(): Field = copy(players =
    players
      .updated(activePlayerId, players(activePlayerId).resetAndIncreaseMana())
      .updated(
        getInactivePlayerId,
        players(getInactivePlayerId).resetAndIncreaseMana()
      )
  )

  override def setPlayerNames(p1: String, p2: String): Field = copy(players =
    players
      .updated(
        activePlayerId,
        players(activePlayerId)
          .setName(p1)
      )
      .updated(getInactivePlayerId, players(getInactivePlayerId).setName(p2))
  )

  override def setHpValues(amount: Int): Field = copy(players =
    players
      .updated(activePlayerId, players(activePlayerId).setHpValue(amount))
      .updated(
        getInactivePlayerId,
        players(getInactivePlayerId).setHpValue(amount)
      )
  )

  override def setManaValues(amount: Int): Field = copy(players =
    players
      .updated(activePlayerId, players(activePlayerId).setManaValue(amount))
      .updated(
        getInactivePlayerId,
        players(getInactivePlayerId).setManaValue(amount)
      )
  )

  override def switchPlayer(): Field = if (turns != 0 && turns % 2 == 1)
  then
    copy(
      players =
        players.mapValues((player) => player.resetAndIncreaseMana()).toMap,
      turns = turns + 1,
      activePlayerId = players.find((id, player) => id != activePlayerId).get._1
    )
  else
    copy(
      activePlayerId =
        players.find((id, player) => id != activePlayerId).get._1,
      turns = turns + 1
    )

  override def getPlayerById(id: Int): Player = players(id)

  override def getActivePlayer: Player = players(activePlayerId)

  override def getInactivePlayerId: Int =
    players.find((id, player) => id != activePlayerId).get._1

  override def reduceDefVal(slotNum: Int, amount: Int): Field = copy(
    players = players.updated(
      getInactivePlayerId,
      players(getInactivePlayerId).reduceDefVal(slotNum, amount)
    )
  )

  override def toMatrix: Matrix[String] = matrix
    .updateMatrix(0, 0, List[String]("-" * FieldObject.standartFieldWidth))
    .updateMatrixWithMatrix(FieldObject.offset, 0, getPlayerById(1).toMatrix)
    .updateMatrixWithMatrix(
      FieldObject.offset + FieldObject.standartMenueBarHeight + FieldObject.standartGameBarHeight
        + FieldObject.standartFieldBarHeight,
      0,
      getPlayerById(2).toMatrix
    )

  override def toString: String =
    toMatrix.rows.map(_.mkString("|", "", "|\n")).mkString

  override def toJson: JsValue = Json.obj(
    "players" -> players.map((id, player) => player.toJson),
    "slotnum" -> Json.toJson(slotNum),
    "turns" -> Json.toJson(turns),
    "activePlayerId" -> Json.toJson(activePlayerId)

  )

  override def toXML: Node =
    <Field>
      <players>
        {
      players.map((id, player) => <entry>
        {player.toXML}
      </entry>)
    }
      </players>
      <slotnum>
        {slotNum.toString}
      </slotnum>
      <turns>
        {turns.toString}
      </turns>
    </Field>
}
