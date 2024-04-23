package persistence

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.*
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.parboiled2.RuleTrace.Run
import akka.testkit.ImplicitSender
import akka.testkit.TestActors
import akka.testkit.TestKit
import akka.testkit.TestProbe
import model.cardComponent.CardInterface
import model.cardComponent.cardImpl.Card
import model.fieldComponent.fieldImpl.Field
import model.playerComponent.playerImpl.Player
import org.checkerframework.checker.units.qual.s
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import persistence.*
import persistence.fileIO.jsonIOImpl.JsonIO
import persistence.fileIO.service.PersistenceService
import play.api.libs.json.Json

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.RouteTestTimeout

class PersistenceServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalatestRouteTest {

  val testCards: List[Card] = List(
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test2", 2, 2, 2, "testEffect2", "testRarety2", 0, "")
  )

  val graveyard: Array[CardInterface] = Array(
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, "")
  )

  val testCardsPlayer1: Player =
    Player("Player1", 1, deck = testCards, friedhof = graveyard)
  val testCardsPlayer2: Player =
    Player("Player2", 2, deck = testCards, friedhof = graveyard)

  val field: Field = Field(players =
    Map[Int, Player](
      (1, testCardsPlayer1),
      (2, testCardsPlayer2)
    )
  )

  "PersistenceService" should {
    "start a server" in {
      val service = new PersistenceService()
      service.start()

      val responseFuture: Future[HttpResponse] =
        Http().singleRequest(HttpRequest(uri = "http://localhost:5001/"))

      responseFuture.onComplete(response => {
        response.get.status shouldEqual StatusCodes.OK
      })

      service.stop()
    }

    "fail to start server when port is in use" in {
      val responseFuture: Future[HttpResponse] =
        Http().singleRequest(HttpRequest(uri = "http://localhost:5001/"))

      responseFuture.onComplete(response => {
        response.get.status shouldEqual 500
      })

    }

    "respond to GET request at the root path" in {
      val service = new PersistenceService()
      service.start()

      Get("/") ~> service.route ~> check {
        responseAs[String] shouldEqual "Persistence Service"
      }

      service.stop()
    }

    "save a field when a POST request is sent" in {
      val service = new PersistenceService()
      service.start()

      Post(
        "/persistence/save",
        Json.prettyPrint(field.toJson)
      ) ~> service.route ~> check {
        responseAs[String] shouldEqual "Saved"
      }

      service.stop()
    }

    "load a field when a GET request is sent " in {
      val service = new PersistenceService()
      service.start()

      Post(
        "/persistence/save",
        Json.prettyPrint(field.toJson)
      )

      Get("/persistence/load") ~> service.route ~> check {
        responseAs[String] shouldEqual Json.prettyPrint(field.toJson)
      }

      service.stop()
    }

    "return error when loading failed" in {
      val mockJsonIO = mock(classOf[JsonIO])
      when(mockJsonIO.load()).thenReturn(
        Failure(new Exception("Error loading field"))
      )

      val service = new PersistenceService(fileIO = mockJsonIO)
      service.start()

      Get("/persistence/load") ~> service.route ~> check {
        status shouldEqual StatusCodes.InternalServerError
        responseAs[String] shouldEqual "Error loading field"
      }

      service.stop()
    }

    "stop the server when a POST request is sent" in {
      val service = new PersistenceService()
      service.start()

      Post("/persistence/stopServer") ~> service.route ~> check {
        responseAs[String] shouldEqual "Server stopped"
      }
    }
  }
}
