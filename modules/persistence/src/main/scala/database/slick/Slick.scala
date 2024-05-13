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
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable

import database.DaoInterface
import scala.annotation.meta.field
import model.fieldComponent.FieldInterface
import scalafx.scene.input.KeyCode.T
import _root_.persistence.database.slick.tables.TestTable

class SlickDatabase extends DaoInterface {
  val db = Database.forURL(
    url = "jdbc:postgresql://localhost:9051/tbl",
    user = "postgres",
    password = "postgres",
    driver = "org.postgresql.Driver"
  )

  protected val connectionRetryAttempts = 5
  protected val maxWaitSeconds = 5.seconds
  protected val currendGameId = 1

  protected def init(setup: DBIOAction[Unit, NoStream, Effect.Schema]): Unit =
    println("Connecting to DB...")
    breakable {
      for (i <- 1 to connectionRetryAttempts)
        Try(Await.result(db.run(setup), maxWaitSeconds)) match
          case Success(_) => println("DB connection established"); break
          case Failure(e) =>
            if e.getMessage.contains("Multiple primary key defined")
            then // ugly workaround: https://github.com/slick/slick/issues/1999
              println("Assuming DB connection established")
              break
            println(
              s"DB connection failed - retrying... - $i/$connectionRetryAttempts"
            )
            println(e.getMessage)
            Thread.sleep(maxWaitSeconds.toMillis)
    }

  override def save(field: FieldInterface): Unit = {
    val insertAction = testTable += ("1", field.toJson)
    Await.result(db.run(insertAction), maxWaitSeconds)
  }

  override def load(): JsValue = ???

  override def update(): Unit = ???

  private def testTable = new TableQuery[TestTable](new TestTable(_))

  init(DBIOAction.seq(testTable.schema.createIfNotExists))

}
