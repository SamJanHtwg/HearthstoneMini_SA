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
import akka.http.javadsl.model.RequestEntity
import akka.http.scaladsl.model.HttpMethod
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes

class ControllerRestClient() extends ControllerInterface {
  private val cardProvider =
    new CardProvider(inputFile = "/json/cards.json")

  private val controllerServiceUrl = "http://localhost:4001/controller"

  var field: FieldInterface = _
  fieldRequest(controllerServiceUrl, "field", HttpMethods.GET)
  var errorMsg: Option[String] = None
  private val undoManager: UndoManager = new UndoManager

  def request(
      endPoint: String,
      command: String,
      method: HttpMethod,
      data: Option[JsValue] = None
  ): Try[JsValue] = {
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
      Await.result(responseJsonFuture, 3.seconds)
    }
  }

  def fieldRequest(
      endpoint: String,
      command: String,
      method: HttpMethod,
      data: Option[JsValue] = None
  ): Unit = {
    val response = request(endpoint, command, method, data).map(Field.fromJson(_))
    response match {
      case Success(newField) =>
        field = newField
        errorMsg = None
        notifyObservers(Event.PLAY, msg = errorMsg)
      case Failure(exception) =>
        errorMsg = Some(exception.getMessage)
        notifyObservers(Event.ERROR, msg = errorMsg)
    }
  }

  def canUndo: Boolean = undoManager.canUndo
  def canRedo: Boolean = undoManager.canRedo

  def placeCard(move: Move): Unit =
    fieldRequest(
      controllerServiceUrl,
      "placeCard",
      HttpMethods.POST,
      Some(move.toJson)
    )

  def drawCard(): Unit =
    fieldRequest(controllerServiceUrl, "drawCard", HttpMethods.GET)

  def setPlayerNames(playername1: String, playername2: String): Unit = {
    fieldRequest(
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

  def setGameState(gameState: GameState): Unit = fieldRequest(
    controllerServiceUrl,
    "setGameState",
    HttpMethods.POST,
    Some(Json.obj("gameState" -> gameState.toString))
  )
  def attack(move: Move): Unit = fieldRequest(
    controllerServiceUrl,
    "attack",
    HttpMethods.POST,
    Some(move.toJson)
  )
  def directAttack(move: Move): Unit = fieldRequest(
    controllerServiceUrl,
    "directAttack",
    HttpMethods.POST,
    Some(move.toJson)
  )
  def switchPlayer(): Unit = fieldRequest(
    controllerServiceUrl,
    "switchPlayer",
    HttpMethods.GET
  )

  def undo: Unit =
    fieldRequest(controllerServiceUrl, "undo", HttpMethods.GET)

  def redo: Unit =
    fieldRequest(controllerServiceUrl, "redo", HttpMethods.GET)

  def exitGame(): Unit =
    fieldRequest(controllerServiceUrl, "exitGame", HttpMethods.GET)

  def setStrategy(strat: Strategy): Unit = fieldRequest(
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
    controllerServiceUrl,
    "save",
    HttpMethods.GET,
  )

  def loadField: Unit = {
    fieldRequest(controllerServiceUrl, "load", HttpMethods.GET)
  }
}
