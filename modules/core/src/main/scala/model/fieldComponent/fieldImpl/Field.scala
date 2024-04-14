package core
package model.fieldComponent.fieldImpl

import model.fieldComponent.FieldInterface
import model.playerComponent.playerImpl.Player
import play.api.libs.json.*

import javax.inject.Inject
import scala.xml.Node

object Field {
  def fromJson(json: JsValue): Field = {
    val fieldJs = json \ "field"
    Field(
      activePlayerId = fieldJs("activePlayerId").toString.toInt,
      players = fieldJs("players")
        .as[Seq[JsValue]]
        .map(player => Player.fromJson(player))
        .map(player => (player.id, player))
        .toMap,
      turns = fieldJs("turns").toString.toInt,
    )
  }

  def fromXml(node: Node): Field =
    Field(
      activePlayerId = (node \ "activePlayerId").text.trim.toInt,
      players = (node \ "players")
        .map(player => node \\ "player")
        .flatten
        .map(player => Player.fromXml(player))
        .map(player => (player.id, player))
        .toMap[Int, Player],
      turns = (node \ "turns").text.trim.toInt
    )
}

//noinspection DuplicatedCode
case class Field @Inject() (
    players: Map[Int, Player] = Map[Int, Player](),
    activePlayerId: Int = 1,
    turns: Int = 0
) extends FieldInterface() {

  def this(player1: String, player2: String) = this(
    players = Map[Int, Player](
      (1, Player(name = player1, id = 1, manaValue = 0, maxManaValue = 1)),
      (2, Player(name = player2, id = 2, manaValue = 0, maxManaValue = 1))
    ),
    activePlayerId = 1
  )

  def this() = this(
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

  override def switchPlayer(): Field = if (turns != 0 && turns % 2 == 1) {
    copy(
      players =
        players.mapValues((player) => player.resetAndIncreaseMana()).toMap,
      turns = turns + 1,
      activePlayerId = getInactivePlayerId
    )
  } else {
    copy(
      activePlayerId = getInactivePlayerId,
      turns = turns + 1
    )
  }

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

  override def toJson: JsValue = Json.obj(
    "players" -> players.map((id, player) => player.toJson),
    "turns" -> Json.toJson(turns),
    "activePlayerId" -> Json.toJson(activePlayerId)
  )

  override def toXML: Node =
    <field>
      <players>
        {players.map((id, player) => player.toXml)}
      </players>
      <turns>
        {turns.toString}
      </turns>
      <activePlayerId>
        {activePlayerId.toString}
      </activePlayerId>
    </field>
}
