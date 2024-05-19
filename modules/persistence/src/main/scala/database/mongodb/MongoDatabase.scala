package persistence.database.mongodb

import org.mongodb.scala._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model._

import scala.collection.JavaConverters._
import persistence.database.DaoInterface
import scala.util.Try
import _root_.model.fieldComponent.FieldInterface
import play.api.libs.json.JsValue

object MongoDatabase extends DaoInterface {

  override def save(field: FieldInterface): Unit = ???

  override def load(): Try[JsValue] = ???

  override def update(field: FieldInterface): Unit = ???

  override def delete(): Try[Unit] = ???

  val client: MongoClient = MongoClient("mongodb://localhost:9061")

}

  