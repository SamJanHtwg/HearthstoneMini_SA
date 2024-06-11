package persistence.database.slick

import play.api.libs.json.JsValue
import slick.dbio.DBIOAction
import slick.dbio.Effect
import slick.dbio.NoStream
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api.*

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.util.control.Breaks._

import persistence.database.DaoInterface
import scala.annotation.meta.field
import model.fieldComponent.FieldInterface
import model.playerComponent.PlayerInterface
import scalafx.scene.input.KeyCode.T
import _root_.persistence.database.slick.tables.GameTable
import _root_.persistence.database.slick.tables.PlayerTable
import play.api.libs.json.Json
import model.playerComponent.playerImpl.Player
import scalafx.scene.input.KeyCode.Play
import model.cardComponent.cardImpl.Card
import scalafx.scene.input.KeyCode.J
import model.fieldComponent.fieldImpl.Field
import model.GameState.GameState
import model.GameState

object SlickDatabase extends DaoInterface {
  private val connectionRetryAttempts = 5
  private val maxWaitSeconds = 5.seconds
  private val player1Id = "100"
  private val player2Id = "200"
  private val gameId = "1"
  private val db = Database.forURL(
    url = "jdbc:postgresql://localhost:9051/postgres",
    user = "postgres",
    password = "postgres",
    driver = "org.postgresql.Driver"
  )

  init(
    DBIO.seq(
      // gameTable.schema.dropIfExists,
      // playerTable.schema.dropIfExists,
      playerTable.schema.createIfNotExists,
      gameTable.schema.createIfNotExists
    )
  )

  private def init(setup: DBIOAction[Unit, NoStream, Effect.Schema]): Unit =
    println("Connecting to DB...")
    breakable {
      for (i <- 1 to connectionRetryAttempts) {
        Try(Await.result(db.run(setup), maxWaitSeconds)) match {
          case Success(_) => {
            println("DB connection established")
            break
          }
          case Failure(e) => {
            if (e.getMessage.contains("Multiple primary key defined")) {
              println("Assuming DB connection established")
              break
            } else {
              println(
                s"DB connection failed - retrying... - $i/$connectionRetryAttempts"
              )
              println(e.getMessage)
              if (i != connectionRetryAttempts) {
                Thread.sleep(maxWaitSeconds.toMillis)
              } else {
                println("Max retry attempts reached. Connection failed.")
              }
            }
          }
        }
      }
    }

  override def save(field: FieldInterface): Unit = {
    val insertGameAction =
      gameTable += (gameId, player1Id, player2Id, field.turns, field.gameState.toString, field.activePlayerId)

    val players = field.players.map((id, player) =>
      (
        s"${player.id}00",
        id,
        player.deck.map(_.toJson),
        player.hand.map(_.toJson),
        player.name,
        player.field.map(slot => slot.map(_.toJson)).toList,
        player.hpValue,
        player.friedhof.map(_.toJson).toList,
        player.manaValue,
        player.maxHpValue,
        player.maxManaValue
      )
    )

    val insertPlayersAction = playerTable ++= players

    val actions = DBIO.seq(insertGameAction, insertPlayersAction)

    val res1 = Await.result(db.run(insertPlayersAction), maxWaitSeconds)
    val res2 = Await.result(db.run(insertGameAction), maxWaitSeconds)
  }

  override def load(): Try[JsValue] = {
    val gameWithPlayersQuery = for {
      ((game, player1), player2) <- gameTable
        .join(playerTable)
        .on(_.player1 === _.key)
        .join(playerTable)
        .on(_._1.player2 === _.key)
      if game.key === gameId
    } yield (game, player1, player2)

    Await
      .result(
        db.run(gameWithPlayersQuery.result.headOption),
        maxWaitSeconds
      )
      .map((game, player1, player2) => {
        val players = Map(
          player1._2 -> Player(
            id = player1._2,
            deck = player1._3.map(Card.fromJson),
            hand = player1._4.map(Card.fromJson),
            name = player1._5,
            field = player1._6.map(slot => slot.map(Card.fromJson)).toVector,
            hpValue = player1._7,
            friedhof = player1._8.map(Card.fromJson).toArray,
            manaValue = player1._9,
            maxHpValue = player1._10,
            maxManaValue = player1._11
          ),
          player2._2 -> Player(
            id = player2._2,
            deck = player2._3.map(Card.fromJson),
            hand = player2._4.map(Card.fromJson),
            name = player2._5,
            field = player2._6.map(slot => slot.map(Card.fromJson)).toVector,
            hpValue = player2._7,
            friedhof = player2._8.map(Card.fromJson).toArray,
            manaValue = player2._9,
            maxHpValue = player2._10,
            maxManaValue = player2._11
          )
        )

        Field(
          players = players,
          activePlayerId = game._6,
          turns = game._4,
          gameState = GameState.withName(game._5)
        ).toJson
      })
      .toRight(new Exception("Game not found"))
      .toTry
  }

  override def delete(): Try[Unit] = {
    val deleteGameAction = gameTable.filter(_.key === gameId).delete
    val deletePlayer1Action = playerTable.filter(_.key === player1Id).delete
    val deletePlayer2Action = playerTable.filter(_.key === player2Id).delete

    val actions =
      DBIO.seq(deleteGameAction, deletePlayer1Action, deletePlayer2Action)

    Try[Unit] {
      Await.result(db.run(deleteGameAction), maxWaitSeconds)
      Await.result(db.run(deletePlayer1Action), maxWaitSeconds)
      Await.result(db.run(deletePlayer2Action), maxWaitSeconds)
    }
  }

  override def update(game: FieldInterface): Unit = {
    val updateGameAction = gameTable
      .filter(_.key === gameId)
      .map(g => (g.turns, g.gameState, g.activePlayerId))
      .update(
        (game.turns, game.gameState.toString, game.activePlayerId)
      )

    val updatePlayer1Action = playerTable
      .filter(_.key === player1Id)
      .update(
        (
          player1Id,
          game.players(1).id,
          game.players(1).deck.map(_.toJson),
          game.players(1).hand.map(_.toJson),
          game.players(1).name,
          game.players(1).field.map(slot => slot.map(_.toJson)).toList,
          game.players(1).hpValue,
          game.players(1).friedhof.map(_.toJson).toList,
          game.players(1).manaValue,
          game.players(1).maxHpValue,
          game.players(1).maxManaValue
        )
      )

    val updatePlayer2Action = playerTable
      .filter(_.key === player2Id)
      .update(
        (
          player2Id,
          game.players(2).id,
          game.players(2).deck.map(_.toJson),
          game.players(2).hand.map(_.toJson),
          game.players(2).name,
          game.players(2).field.map(slot => slot.map(_.toJson)).toList,
          game.players(2).hpValue,
          game.players(2).friedhof.map(_.toJson).toList,
          game.players(2).manaValue,
          game.players(2).maxHpValue,
          game.players(2).maxManaValue
        )
      )

    Await.result(db.run(updatePlayer1Action), maxWaitSeconds)
    Await.result(db.run(updatePlayer2Action), maxWaitSeconds)
    Await.result(db.run(updateGameAction), maxWaitSeconds)
  }

  private def gameTable = new TableQuery[GameTable](new GameTable(_))
  private def playerTable = new TableQuery[PlayerTable](new PlayerTable(_))

}
