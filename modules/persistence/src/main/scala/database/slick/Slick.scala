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
import scalafx.scene.input.KeyCode.T
import _root_.persistence.database.slick.tables.TestTable

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
      testTable.schema.createIfNotExists
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
    val insertAction = testTable += ("1", field.toJson)
    var res = Await.result(db.run(insertAction), maxWaitSeconds)
    println(res)
  }

  override def load(): Try[JsValue] = {
    val query = testTable.filter(_.key === "1").result.headOption
    val res = Await.result(db.run(query), maxWaitSeconds)
    res.map(_._2).toRight(new Exception("No data found")).toTry
  }

  override def update(): Unit = ???

  private def testTable = new TableQuery[TestTable](new TestTable(_))

}
