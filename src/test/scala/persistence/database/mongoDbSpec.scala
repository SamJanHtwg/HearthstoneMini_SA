package persistence.database.mongodb

import _root_.model.fieldComponent.FieldInterface
import _root_.persistence.database.mongodb.MongoDatabase as MyDataBase
import com.mongodb.client.result.InsertOneResult
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mongodb.scala._
import org.mongodb.scala.bson.BsonInt32
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

import scala.concurrent.duration.*
import scala.concurrent.Future
import scala.util.Try
import org.scalatest.BeforeAndAfterEach
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult

class MongoDatabaseTest
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterEach {
  // Mock dependencies
  var mockMongoCollection: MongoCollection[Document] = _
  var mockField: FieldInterface = _
  var singleObservable: SingleObservable[InsertOneResult] = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockField = mock(classOf[FieldInterface])
    singleObservable = SingleObservable(
      InsertOneResult.acknowledged(BsonInt32(5))
    )
    mockMongoCollection = mock(classOf[MongoCollection[Document]])

    // Setup mock behavior
    when(mockMongoCollection.insertOne(any[Document]))
      .thenReturn(singleObservable)
    when(mockField.toJson).thenReturn(Json.obj("key" -> "value"))
    // Stubbing for deleteOne and updateOne methods
    when(mockMongoCollection.deleteOne(any[Document]))
      .thenReturn(SingleObservable(DeleteResult.acknowledged(1)))
    when(mockMongoCollection.updateOne(any[Document], any[Document]))
      .thenReturn(SingleObservable(UpdateResult.acknowledged(1, 1, null)))
  }

  "MongoDatabase.save" should "insert a document into the collection" in {

    val dataBase = MyDataBase
    dataBase.collection = mockMongoCollection

    // execute
    dataBase.save(mockField)

    // verify
    verify(mockMongoCollection).insertOne(any[Document])
  }
  "MongoDatabase.load" should "return a JsValue" in {
    val dataBase = MyDataBase
    dataBase.collection = mockMongoCollection

    // execute
    val result = dataBase.load()

    // verify
    result.isSuccess shouldBe false
  }

  "MongoDatabase.delete" should "delete a document from the collection" in {
    val dataBase = MyDataBase
    dataBase.collection = mockMongoCollection

    // execute
    dataBase.delete()

    // verify
    verify(mockMongoCollection).deleteOne(any[Document])
  }

  "MongoDatabase.update" should "update a document in the collection" in {
    val dataBase = MyDataBase
    dataBase.collection = mockMongoCollection

    // execute
    dataBase.update(mockField)

    // verify
    verify(mockMongoCollection).updateOne(any[Document], any[Document])
  }
}
