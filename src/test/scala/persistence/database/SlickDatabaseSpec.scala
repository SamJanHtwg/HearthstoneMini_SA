package persistence.database

import org.scalatest.BeforeAndAfterAll
import persistence.database.slick.SlickDatabase
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.mockito.Mockito.times
import org.mockito.Mockito.mock
import _root_.slick.jdbc.JdbcBackend
import org.scalatest.wordspec.AnyWordSpec
import model.fieldComponent.fieldImpl.Field
import scala.concurrent.Future
import _root_.slick.dbio.Effect.Schema
import _root_.slick.dbio.NoStream
import _root_.slick.dbio.DBIOAction
import _root_.slick.sql.SqlAction
import play.api.libs.json.JsValue
import _root_.slick.dbio.Effect.*

class SlickDatabaseSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterEach {
  var mockDatabase: JdbcBackend.Database = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockDatabase = mock(classOf[JdbcBackend.Database])

    when(mockDatabase.run(any[DBIOAction[Unit, NoStream, Schema]]))
      .thenReturn(Future.unit)
    when(
      mockDatabase.run(
        any[SqlAction[
          Option[
            (
                (String, String, String, Int, String, Int),
                (
                    String,
                    Int,
                    List[JsValue],
                    List[JsValue],
                    String,
                    List[Option[JsValue]],
                    Int,
                    List[JsValue],
                    Int,
                    Int,
                    Int
                ),
                (
                    String,
                    Int,
                    List[JsValue],
                    List[JsValue],
                    String,
                    List[Option[JsValue]],
                    Int,
                    List[JsValue],
                    Int,
                    Int,
                    Int
                )
            )
          ],
          NoStream,
          Read
        ]]
      )
    ).thenReturn(
      Future.successful[Option[
        (
            (String, String, String, Int, String, Int),
            (
                String,
                Int,
                List[JsValue],
                List[JsValue],
                String,
                List[Option[JsValue]],
                Int,
                List[JsValue],
                Int,
                Int,
                Int
            ),
            (
                String,
                Int,
                List[JsValue],
                List[JsValue],
                String,
                List[Option[JsValue]],
                Int,
                List[JsValue],
                Int,
                Int,
                Int
            )
        )
      ]](
        Some(
          (
            ("1", "100", "200", 0, "1", 0),
            (
              "100",
              1,
              List.empty,
              List.empty,
              "1",
              List.empty,
              0,
              List.empty,
              0,
              0,
              0
            ),
            (
              "200",
              2,
              List.empty,
              List.empty,
              "1",
              List.empty,
              0,
              List.empty,
              0,
              0,
              0
            )
          )
        )
      )
    )
  }

  "SlickDatabase" should {
    "initialize the database" in {
      new SlickDatabase(mockDatabase)
    }
    "call db to save field" in {
      val slickDatabase = new SlickDatabase(mockDatabase)
      slickDatabase.save(Field())

      verify(mockDatabase, times(2)).run(any())
    }
  }
    "call db to delete field" in {
        val slickDatabase = new SlickDatabase(mockDatabase)
        slickDatabase.delete()
    
        verify(mockDatabase, times(2)).run(any())
    }

}
