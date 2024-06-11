package persistence.database.mongodb

import _root_.model.fieldComponent.FieldInterface
import _root_.persistence.database.mongodb.MongoDBDatabase
import com.mongodb.client.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito._
import org.mongodb.scala._
import org.mongodb.scala.bson.BsonInt32
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import org.mockito.Mockito
import scala.concurrent.Future
import scalafx.scene.input.KeyCode.B
import org.bson.conversions.Bson

class MongoDatabaseTest
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterEach {
  var mockClient: MongoClient = _
  var mockMongoDatabase: MongoDatabase = _
  var mockMongoCollection: MongoCollection[Document] = _
  
  var mockField: FieldInterface = _
  // Observables
  var singleObservable: SingleObservable[Document] = _
  var singleInsertObservable: SingleObservable[InsertOneResult] = _
  var singleDeleteObservable: SingleObservable[DeleteResult] = _
  var singleUpdateObservable: SingleObservable[UpdateResult] = _
  var mockFindObservable: FindObservable[Document] = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockField = mock(classOf[FieldInterface])

    singleObservable = mock(classOf[SingleObservable[Document]])
    singleInsertObservable = mock(classOf[SingleObservable[InsertOneResult]])
    singleDeleteObservable = mock(classOf[SingleObservable[DeleteResult]])
    singleUpdateObservable = mock(classOf[SingleObservable[UpdateResult]])
    mockMongoCollection = mock(classOf[MongoCollection[Document]])
    mockFindObservable = mock(classOf[FindObservable[Document]])

    mockMongoDatabase = mock(classOf[MongoDatabase])
    mockClient = mock(classOf[MongoClient])

    // Setup mock behavior
    when(mockClient.getDatabase(any[String]))
      .thenReturn(mockMongoDatabase)
    when(mockMongoDatabase.createCollection(any[String]))
      .thenReturn(singleObservable)
    when(mockMongoDatabase.getCollection(any())(any(), any()))
      .thenReturn(mockMongoCollection)
    
    when(singleObservable.toFuture()).thenReturn(Future.successful(Document()))
    when(singleInsertObservable.toFuture())
      .thenReturn(Future.successful(InsertOneResult.acknowledged(BsonInt32(1))))
    when(singleDeleteObservable.toFuture())
      .thenReturn(Future.successful(DeleteResult.acknowledged(1)))
    when(singleUpdateObservable.toFuture())
      .thenReturn(Future.successful(UpdateResult.acknowledged(1,1, BsonInt32(1))))

    when(mockMongoCollection.insertOne(any[Document]))
      .thenReturn(singleInsertObservable)
    when(mockMongoCollection.deleteOne(any[Document]))
      .thenReturn(singleDeleteObservable)
    when(mockMongoCollection.updateOne(any[Document], any[Document]))
      .thenReturn(singleUpdateObservable)
    when(mockField.toJson).thenReturn(Json.obj("key" -> "value"))
    when(mockMongoCollection.find(any[Document])(any(), any()))
      .thenReturn(mockFindObservable)
    when(mockFindObservable.headOption())
      .thenReturn(
        Future.successful(Some(Document("game" -> """{"key":"value"}""")))
      )
  }
  "MongoDatabase" should "initialize" in {
    val dataBase = MongoDBDatabase()
    dataBase shouldBe a[MongoDBDatabase]
  } 

  "MongoDatabase.save" should "insert a document into the collection" in {
    val dataBase = new MongoDBDatabase(using mockClient)
    // execute
    dataBase.save(mockField)

    // verify
    verify(mockMongoCollection).insertOne(any[Document])
  }

  "MongoDatabase.load" should "return a JsValue" in {
    val dataBase = new MongoDBDatabase(using mockClient)

    // execute
    val result = dataBase.load()

    // verify
    result.isSuccess shouldBe true
    result.get.toString should include(""""key":"value"""")
  }

  "MongoDatabase.delete" should "delete a document from the collection" in {
    val dataBase = new MongoDBDatabase(using mockClient)

    // execute
    val result = dataBase.delete()

    // verify
    result.isSuccess shouldBe true
    verify(mockMongoCollection).deleteOne(any[Document])
  }

  "MongoDatabase.update" should "update a document in the collection" in {
    val dataBase = new MongoDBDatabase(using mockClient)

    // execute
    dataBase.update(mockField)

    // verify
    verify(mockMongoCollection).updateOne(any[Document], any[Document])
  }
}
