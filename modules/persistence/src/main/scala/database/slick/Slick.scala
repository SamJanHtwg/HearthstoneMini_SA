package persistence
package database.slick

import database.DaoInterface
import play.api.libs.json.JsValue

import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.PostgresProfile.api._

class SlickDatabase extends DaoInterface {
  val db = Database.forURL("database")
  override def save(json: JsValue): Unit = ???

  override def load(): JsValue = ???

  override def update(): Unit = ???

    
}