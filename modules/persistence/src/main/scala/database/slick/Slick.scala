package persistence
package database.slick

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

import database.DaoInterface
import scala.annotation.meta.field
import model.fieldComponent.FieldInterface
import model.playerComponent.PlayerInterface
import scalafx.scene.input.KeyCode.T
import _root_.persistence.database.slick.tables.TestTable
import _root_.persistence.database.slick.tables.PlayerTable
import play.api.libs.json.Json

object SlickDatabase extends DaoInterface {
  private val connectionRetryAttempts = 5
  private val maxWaitSeconds = 5.seconds

  val db = Database.forURL(
    url = "jdbc:postgresql://localhost:9051/postgres",
    user = "postgres",
    password = "postgres",
    driver = "org.postgresql.Driver"
  )

  init(
    DBIO.seq(
      // testTable.schema.dropIfExists,
      testTable.schema.createIfNotExists,
      playerTable.schema.dropIfExists,
      playerTable.schema.createIfNotExists
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
    // val insertAction = testTable += ("2", field.toJson)

    val player1Id = "100"
    val player2Id = "200"

    val player1 =
      (
        player1Id,
        Json.stringify(Json.toJson(field.players(0).deck.map(_.toJson))),
        Json.stringify(Json.toJson(field.players(0).hand.map(_.toJson))),
        field.players(0).name,
        field.players(0).toJson,
        field.players(0).hpValue,
        Json.stringify(Json.toJson(field.players(0).friedhof.map(_.toJson))),
        field.players(0).manaValue,
        field.players(0).maxHpValue,
        field.players(0).maxManaValue
      )

    val insertPlayersAction = playerTable += player1
    Await.result(db.run(insertPlayersAction), maxWaitSeconds)

    // var res = Await.result(db.run(insertAction), maxWaitSeconds)
    // println(res)

    // res = Await.result(db.run(player1), maxWaitSeconds)
  }

  override def load(): Try[JsValue] = {
    val query = testTable.filter(_.key === "1").result.headOption
    val res = Await.result(db.run(query), maxWaitSeconds)
    res.map(_._2).toRight(new Exception("No data found")).toTry
  }

  override def update(): Unit = ???

  private def testTable = new TableQuery[TestTable](new TestTable(_))
  private def playerTable = new TableQuery[PlayerTable](new PlayerTable(_))

}
