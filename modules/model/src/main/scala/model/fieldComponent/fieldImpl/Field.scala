package model
package fieldComponent.fieldImpl

import model.GameState.*
import model.GameState
import fieldComponent.FieldInterface
import playerComponent.playerImpl.Player
import play.api.libs.json.*

import javax.inject.Inject
import model.playerComponent.PlayerInterface

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
      gameState =
        GameState.withName(fieldJs("gameState").toString.replace("\"", ""))
    )
  }
}

//noinspection DuplicatedCode
case class Field(
    players: Map[Int, PlayerInterface] = Map[Int, PlayerInterface](),
    activePlayerId: Int = 1,
    turns: Int = 0,
    gameState: GameState = GameState.CHOOSEMODE
) extends FieldInterface() {

  override def setGameState(gameState: GameState.GameState): FieldInterface =
    copy(gameState = gameState)

  def this() = this(
    activePlayerId = 1,
    players = Map[Int, PlayerInterface](
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

  override def getPlayerById(id: Int): PlayerInterface = players(id)

  override def getActivePlayer: PlayerInterface = players(activePlayerId)

  override def getInactivePlayerId: Int =
    players.find((id, player) => id != activePlayerId).get._1

  override def reduceDefVal(slotNum: Int, amount: Int): Field = copy(
    players = players.updated(
      getInactivePlayerId,
      players(getInactivePlayerId).reduceDefVal(slotNum, amount)
    )
  )

  override def toJson: JsValue = Json.obj(
    "field" -> Json.obj(
      "activePlayerId" -> Json.toJson(activePlayerId),
      "players" -> Json.toJson(players.values.map(player => player.toJson)),
      "turns" -> Json.toJson(turns),
      "gameState" -> Json.toJson(gameState.toString)
    )
  )
}
