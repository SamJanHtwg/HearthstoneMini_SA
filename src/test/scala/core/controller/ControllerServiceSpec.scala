package core.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.*
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.RouteTestTimeout
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.parboiled2.RuleTrace.Run
import akka.stream.Materializer
import akka.stream.javadsl.FileIO
import akka.stream.scaladsl.Source
import akka.testkit.ImplicitSender
import akka.testkit.TestActors
import akka.testkit.TestKit
import akka.testkit.TestProbe
import core.controller.Strategy.Strategy
import core.controller.component.ControllerInterface
import core.controller.component.ControllerServiceInterface
import core.controller.service.HttpService
import core.controller.service.RestControllerService
import core.util.CardProvider
import core.util.UndoManager
import io.gatling.core.config.ConfigKeys.http
import model.GameState
import model.GameState.GameState
import model.Move
import model.cardComponent.CardInterface
import model.cardComponent.cardImpl.Card
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import model.playerComponent.PlayerInterface
import model.playerComponent.playerImpl.Player
import org.checkerframework.checker.units.qual.s
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import persistence.database.DaoInterface
import persistence.fileIO.FileIOInterface
import persistence.fileIO.jsonIOImpl.JsonIO
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import spray.json.JsString

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class ControllerServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalatestRouteTest
    with BeforeAndAfterEach {

  var testCards: List[Card] = _
  var mockUndoManager: UndoManager = _
  var mockCardProvider: CardProvider = _
  var mockFileIO: FileIOInterface = _
  var controllerService: RestControllerService = _
  var mockHttpService: HttpService = _
  var mockField: FieldInterface = _
  var mockController: ControllerInterface = _
  var mockDao: DaoInterface = _
  var mockMaterializer: Materializer = _
  var mockControllerService: ControllerServiceInterface = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockCardProvider = CardProvider(inputFile = "/json/cards.json")
    mockUndoManager = mock(classOf[UndoManager])
    mockControllerService = mock(classOf[ControllerServiceInterface])
    mockFileIO = mock(classOf[FileIOInterface])
    mockController = mock(classOf[ControllerInterface])
    mockHttpService = mock(classOf[HttpService])
    mockDao = mock(classOf[DaoInterface])
    mockMaterializer = mock(classOf[Materializer])

    testCards = List[Card](
      Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
      Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
      Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
      Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
      Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, "")
    )
  }

  "Controller Service" should {
    "return message when calling GET /" in {
      val service = new RestControllerService(using  mockHttpService)
      service.start()
      Get("/") ~> service.route ~> check {
        responseAs[
          String
        ] shouldEqual "HearthstoneMini ControllerAPI Service is online."
      }

      service.stop()
    }

    "return game state when calling GET /controller/gameState" in {
      val service = new RestControllerService(using  mockHttpService)
      service.start()
      Get("/controller/gameState") ~> service.route ~> check {
        responseAs[String] shouldEqual GameState.CHOOSEMODE.toString()
      }

      service.stop()
    }

    "return field when calling GET /controller/field" in {
      val service = new RestControllerService(using  mockHttpService)
      service.start()
      Get("/controller/field") ~> service.route ~> check {
        responseAs[String] shouldEqual mockController.field.toJson.toString()
      }

      service.stop()
    }

    "save field when calling GET /controller/save" in {
      when(mockHttpService.request(any(), any(), any(), any())).thenReturn(Success(Json.toJson("Saved")))
      val service = new RestControllerService(using  mockHttpService)
      service.start()
      

      Get("/controller/save") ~> service.route ~> check {

        responseAs[String] shouldEqual "success"
      }

      service.stop()
    }

    "load field" in {
      when(mockFileIO.load()).thenReturn(Success(mockController.field))
      when(mockDao.load()).thenReturn(Success(mockController.field))
      when(mockHttpService.request(any(), any(), any(), any())).thenReturn(Success(mockController.field.toJson))
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Get("/controller/load") ~> service.route ~> check {
        responseAs[String] shouldEqual mockController.field.toJson.toString()
      }

      service.stop()
    }

    "draw card" in {
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Get("/controller/drawCard") ~> service.route ~> check {
        responseAs[String] shouldEqual mockController.field.toJson.toString()
      }

      service.stop()
    }

    "switch player" in {
      val service = new RestControllerService(using  mockHttpService)

      service.start()

      Get("/controller/switchPlayer") ~> service.route ~> check {
        responseAs[String] shouldEqual mockController.field.toJson.toString()
      }

      service.stop()
    }

    "can undo" in {
      when(mockUndoManager.canUndo()).thenReturn(false)

      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Get("/controller/canUndo") ~> service.route ~> check {
        responseAs[String] shouldEqual "false"
      }

      service.stop()
    }

    "can redo" in {
      when(mockUndoManager.canRedo()).thenReturn(false)

      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Get("/controller/canRedo") ~> service.route ~> check {
        responseAs[String] shouldEqual "false"
      }

      service.stop()
    }

    "undo" in {
      when(mockUndoManager.undoStep(any())).thenReturn(Success(mockController.field))
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Get("/controller/undo") ~> service.route ~> check {
        responseAs[String] shouldEqual mockController.field.toJson.toString()
      }

      service.stop()
    }

    "redo" in {
      when(mockUndoManager.redoStep(any())).thenReturn(Success(mockController.field))
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Get("/controller/redo") ~> service.route ~> check {
        responseAs[String] shouldEqual mockController.field.toJson.toString()
      }

      service.stop()
    }

    "exit game" in {
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Get("/controller/exitGame") ~> service.route ~> check {
        responseAs[String] shouldEqual mockController.field.toJson.toString()
      }

      service.stop()
    }

    "return error message when calling invalid command" in {
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Get("/controller/invalid") ~> service.route ~> check {
        response.status shouldEqual StatusCodes.InternalServerError
      }

      service.stop()
    }

    "post place card" in {
      mockController.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1, manaValue = 100, hand = testCards)),
          (2, Player(id = 2))
        )
      )
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Post(
        "/controller/placeCard",
        new Move().toJson.toString()
      ) ~> service.route ~> check {
        response.status shouldEqual StatusCodes.OK
      }

      service.stop()
    }

    "set player names" in {
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Post(
        "/controller/setPlayerNames",
        Json
          .obj("playername1" -> "Player1", "playername2" -> "Player2")
          .toString()
      ) ~> service.route ~> check {
        response.status shouldEqual StatusCodes.OK
      }

      service.stop()
    }

    "set game state" in {
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Post(
        "/controller/setGameState",
        Json.obj("gameState" -> "CHOOSEMODE").toString()
      ) ~> service.route ~> check {
        response.status shouldEqual StatusCodes.OK
      }

      service.stop()
    }

    "attack" in {
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Post(
        "/controller/attack",
        new Move().toJson.toString()
      ) ~> service.route ~> check {
        response.status shouldEqual StatusCodes.OK
      }

      service.stop()
    }

    "direct attack" in {
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Post(
        "/controller/directAttack",
        new Move().toJson.toString()
      ) ~> service.route ~> check {
        response.status shouldEqual StatusCodes.OK
      }

      service.stop()
    }

    "set strategy" in {
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Post(
        "/controller/setStrategy",
        Json.obj("strategy" -> "debug").toString()
      ) ~> service.route ~> check {
        response.status shouldEqual StatusCodes.OK
      }

      service.stop()
    }

    "return error message when calling invalid post command" in {
      val service = new RestControllerService(using  mockHttpService)
      service.start()

      Post(
        "/controller/invalid",
        JsString("ahla").toString
      ) ~> service.route ~> check {
        status shouldEqual StatusCodes.OK
      }

      service.stop()
    }

  }

}
