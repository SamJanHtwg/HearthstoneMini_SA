package persistence.database.mongodb

import org.mongodb.scala._
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
import util.Observable
import com.mongodb.connection.ServerSettings
import scala.concurrent.Await
import scala.concurrent.duration.*
import play.api.libs.json.Json

object MongoDatabase extends DaoInterface with Observable {
  private val endpoint: String = "mongodb://localhost:9061"
  private val databaseName: String = "hearthstone"
  private val collectionName: String = "games"

  private val client: MongoClient = MongoClient(
    MongoClientSettings
      .builder(
      )
      .applyConnectionString(new ConnectionString(endpoint))
      .credential(
        MongoCredential.createCredential(
          "root",
          "admin",
          "root".toCharArray()
        )
      )
      .build()
  )
  private val database: MongoDatabase = client.getDatabase(databaseName)
  Await.result(
    database.createCollection(collectionName).toFuture(),
    100.seconds
  )

  override def save(field: FieldInterface): Unit = {
    val document = Document("game" -> field.toJson.toString, "_id" -> 1)
    Await.result(
      database.getCollection(collectionName).insertOne(document).toFuture(),
      3.seconds
    )
  }

  override def load(): Try[JsValue] = {
    Try(
      Await
        .result(
          database
            .getCollection(collectionName)
            .find(equal("_id", 1))
            .headOption(),
          300.seconds
        )
        .map(document => {
          println(document)
          document
        })
        .toRight(Exception("No game found"))
        .map(document => Json.parse(document.getString("game")))
        .toTry
    ).flatMap(data => data)
  }

  override def update(field: FieldInterface): Unit = {
    Await.result(
      database
        .getCollection(collectionName)
        .updateOne(equal("_id", 1), set("game", field.toJson.toString))
        .toFuture(),
      3.seconds
    )
  }

  override def delete(): Try[Unit] =
    Try[Unit](
      Await.result(
        database
          .getCollection(collectionName)
          .deleteOne(equal("_id", 1))
          .toFuture(),
        3.seconds
      )
    )
}
