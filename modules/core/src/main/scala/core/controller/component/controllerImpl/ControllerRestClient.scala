package core.controller.component.controllerImpl

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.javadsl.model.RequestEntity
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethod
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.*
import core.controller.Strategy
import core.controller.Strategy.Strategy
import core.controller.component.ControllerInterface
import core.controller.service.HttpService
import core.util.CardProvider
import util.{Event, Observable, Observer}
import core.util.UndoManager
import core.util.commands.CommandInterface
import core.util.commands.commandImpl.AttackCommand
import core.util.commands.commandImpl.DirectAttackCommand
import core.util.commands.commandImpl.DrawCardCommand
import core.util.commands.commandImpl.PlaceCardCommand
import core.util.commands.commandImpl.SwitchPlayerCommand
import model.GameState
import model.GameState.*
import model.Move
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import model.playerComponent.playerImpl.Player
import play.api.libs.json.*

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.*
import scala.util.Failure
import scala.util.Success
import scala.util.Try
class ControllerRestClient(using httpService: HttpService)
    extends ControllerInterface {
  private val controllerServiceUrl = "http://localhost:9031/controller"

  def connectToWebsocket: Unit = {
    httpService.connectToWebSocket(
      controllerServiceUrl.replace("http", "ws") + "/ws",
      msg => {
        if (msg.isStrict) {
          field = Field.fromJson(Json.parse(msg.asTextMessage.getStrictText))
          errorMsg = None
          notifyObservers(Event.PLAY, msg = errorMsg)
        }
      }
    )
  }

  connectToWebsocket

  var field: FieldInterface = _
  setState(fieldRequest(controllerServiceUrl, "field", HttpMethods.GET))
  var errorMsg: Option[String] = None

  private def fieldRequest(
      endpoint: String,
      command: String,
      method: HttpMethod,
      data: Option[JsValue] = None
  ): Try[FieldInterface] = {
    httpService
      .request(endpoint, command, method, data)
      .map(Field.fromJson)
  }

  private def setState(newField: Try[FieldInterface]): Unit = newField match {
    case Success(newField) =>
      field = newField
      errorMsg = None
      notifyObservers(Event.PLAY, msg = errorMsg)
    case Failure(exception) =>
      errorMsg = Some(exception.getMessage)
      notifyObservers(Event.ERROR, msg = errorMsg)

  }

  def deleteField: Unit =
    httpService.request(
      controllerServiceUrl,
      "delete",
      HttpMethods.GET
    )

  def canUndo: Boolean =
    httpService.request(
      controllerServiceUrl,
      "canUndo",
      HttpMethods.GET
    ) match {
      case Success(json) => json.as[Boolean]
      case Failure(_)    => false
    }

  def canRedo: Boolean =
    httpService.request(
      controllerServiceUrl,
      "canRedo",
      HttpMethods.GET
    ) match {
      case Success(json) => json.as[Boolean]
      case Failure(_)    => false
    }

  def placeCard(move: Move): Unit =
    setState(
      fieldRequest(
        controllerServiceUrl,
        "placeCard",
        HttpMethods.POST,
        Some(move.toJson)
      )
    )

  def drawCard(): Unit =
    setState(fieldRequest(controllerServiceUrl, "drawCard", HttpMethods.GET))

  def setPlayerNames(playername1: String, playername2: String): Unit = {
    setState(
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
    )
  }

  def setGameState(gameState: GameState): Unit = setState(
    fieldRequest(
      controllerServiceUrl,
      "setGameState",
      HttpMethods.POST,
      Some(Json.obj("gameState" -> gameState.toString))
    )
  )
  def attack(move: Move): Unit = setState(
    fieldRequest(
      controllerServiceUrl,
      "attack",
      HttpMethods.POST,
      Some(move.toJson)
    )
  )
  def directAttack(move: Move): Unit = setState(
    fieldRequest(
      controllerServiceUrl,
      "directAttack",
      HttpMethods.POST,
      Some(move.toJson)
    )
  )
  def switchPlayer(): Unit = setState(
    fieldRequest(
      controllerServiceUrl,
      "switchPlayer",
      HttpMethods.GET
    )
  )

  def undo: Unit =
    setState(fieldRequest(controllerServiceUrl, "undo", HttpMethods.GET))

  def redo: Unit =
    setState(fieldRequest(controllerServiceUrl, "redo", HttpMethods.GET))

  def exitGame(): Unit =
    setState(fieldRequest(controllerServiceUrl, "exitGame", HttpMethods.GET))

  def setStrategy(strat: Strategy): Unit = setState(
    fieldRequest(
      controllerServiceUrl,
      "setStrategy",
      HttpMethods.POST,
      Some(Json.obj("strategy" -> strat.toString))
    )
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

  def saveField: Unit = httpService.request(
    controllerServiceUrl,
    "save",
    HttpMethods.GET
  )

  def loadField: Unit =
    setState(fieldRequest(controllerServiceUrl, "load", HttpMethods.GET))

}
