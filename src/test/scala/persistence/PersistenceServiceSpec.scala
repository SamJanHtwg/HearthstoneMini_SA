package persistence

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.ImplicitSender
import akka.testkit.TestActors
import akka.testkit.TestKit
import akka.testkit.TestProbe
import model.cardComponent.CardInterface
import model.cardComponent.cardImpl.Card
import model.fieldComponent.fieldImpl.Field
import model.playerComponent.playerImpl.Player
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import persistence.*
import persistence.fileIO.service.PersistenceService
import play.api.libs.json.Json
import org.mockito.Mockito.mock
import persistence.fileIO.jsonIOImpl.JsonIO

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

    "stop the server when a POST request is sent" in {
      val service = new PersistenceService()
      service.start()

      Post("/persistence/stopServer") ~> service.route ~> check {
        responseAs[String] shouldEqual "Server stopped"
      }
    }
  }

}
