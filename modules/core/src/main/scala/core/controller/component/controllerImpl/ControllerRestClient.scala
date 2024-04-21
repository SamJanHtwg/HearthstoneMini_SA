package core.controller.component.controllerImpl
import core.controller.Strategy
import core.controller.Strategy.Strategy
import model.GameState.*
import model.GameState
import core.controller.component.ControllerInterface
import model.fieldComponent.FieldInterface
import model.Move
import model.playerComponent.playerImpl.Player
import core.util.{Event, Observable, UndoManager}
import core.util.commands.CommandInterface
import model.fieldComponent.fieldImpl.Field
import scala.util.{Failure, Success, Try}
import core.util.commands.commandImpl.{
  PlaceCardCommand,
  DirectAttackCommand,
  DrawCardCommand,
  SwitchPlayerCommand,
  AttackCommand
}
import core.util.CardProvider
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.unmarshalling.Unmarshal
import play.api.libs.json.*
import scala.concurrent.Await
import scala.concurrent.duration.*
import com.fasterxml.jackson.databind.util.JSONPObject
import akka.http.javadsl.model.RequestEntity
import akka.http.scaladsl.model.HttpMethod
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes
import persistence.fileIO.FileIOInterface

class ControllerRestClient(val fileIO: FileIOInterface)
    extends ControllerInterface {
  private val cardProvider =
    new CardProvider(inputFile = "/json/cards.json")

  private val controllerServiceUrl = "http://localhost:4001/controller"
  private val persistenceServiceEndpoint = "http://localhost:5001/persistence"

  var field: FieldInterface = Field(
    players = Map(
      1 -> Player(
        id = 1,
        hand = cardProvider.getCards(5),
        deck = cardProvider.getCards(30)
      ),
      2 -> Player(
        id = 2,
        hand = cardProvider.getCards(5),
        deck = cardProvider.getCards(30)
      )
    )
  )
  var errorMsg: Option[String] = None
  private val undoManager: UndoManager = new UndoManager

  def request(
      endPoint: String,
      command: String,
      method: HttpMethod,
      data: Option[JsValue] = None
  ): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
    implicit val executionContext = system.executionContext

    val responseFuture = Http().singleRequest(
      HttpRequest(
        method = method,
        uri = s"$endPoint/$command",
        entity = data match {
          case Some(data) =>
            HttpEntity(ContentTypes.`application/json`, Json.stringify(data))
          case None =>
            HttpEntity.Empty
        }
      )
    )

    val responseJsonFuture = responseFuture.flatMap { response =>
      Unmarshal(response.entity).to[String].map { jsonString =>
        Json.parse(jsonString)
      }
    }
    Try {
      val responseJson =
        Await.result(responseJsonFuture, 30.seconds)
      Field.fromJson(responseJson)
    } match {
      case Success(newField) => {
        field = newField
        errorMsg = None
        notifyObservers(Event.PLAY, msg = errorMsg)
      }
      case Failure(x) =>
        errorMsg = Some(x.getMessage)
        notifyObservers(Event.ERROR, msg = errorMsg)
    }
  }

  def canUndo: Boolean = undoManager.canUndo
  def canRedo: Boolean = undoManager.canRedo

  def placeCard(move: Move): Unit =
    request(
      controllerServiceUrl,
      "placeCard",
      HttpMethods.POST,
      Some(move.toJson)
    )

  def drawCard(): Unit =
    request(controllerServiceUrl, "drawCard", HttpMethods.GET)

  def setPlayerNames(playername1: String, playername2: String): Unit = {
    request(
      controllerServiceUrl,
      "setPlayerNames",
      HttpMethods.POST,
      Some(
        Json.obj(
          "playername1" -> playername1,
          "playername2" -> playername2
        )
      )
    )
  }

  def attack(move: Move): Unit = request(
    controllerServiceUrl,
    "attack",
    HttpMethods.POST,
    Some(move.toJson)
  )
  def directAttack(move: Move): Unit = request(
    controllerServiceUrl,
    "directAttack",
    HttpMethods.POST,
    Some(move.toJson)
  )
  def switchPlayer(): Unit = request(
    controllerServiceUrl,
    "switchPlayer",
    HttpMethods.GET
  )

  def undo: Unit =
    request(controllerServiceUrl, "undo", HttpMethods.GET)

  def redo: Unit =
    request(controllerServiceUrl, "redo", HttpMethods.GET)

  def exitGame(): Unit =
    request(controllerServiceUrl, "exitGame", HttpMethods.GET)

  def setStrategy(strat: Strategy): Unit = request(
    controllerServiceUrl,
    "setStrategy",
    HttpMethods.POST,
    Some(Json.obj("strategy" -> strat.toString))
  )

  def getWinner(): Option[String] = {
    val playersWithHp = field.players.filterNot(_._2.isHpEmpty)
    playersWithHp.values.size match {
      case 1 =>
        field = field.setGameState(GameState.WIN)
        Some(playersWithHp.values.head.name)
      case _ => None
    }
  }

  def saveField: Unit = request(
    persistenceServiceEndpoint,
    "save",
    HttpMethods.POST,
    Some(field.toJson)
  )

  def loadField: Unit = {
    request(persistenceServiceEndpoint, "load", HttpMethods.GET)
  }
}
