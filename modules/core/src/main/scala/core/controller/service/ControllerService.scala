package core.controller.service

import akka.Done
import akka.NotUsed
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.StandardRoute
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.*
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import core.controller.Strategy
import core.controller.Strategy.*
import core.controller.component
import core.controller.component.BackendServiceInterface
import core.controller.component.ControllerInterface
import core.controller.component.GetFieldMessage
import core.controller.component.ServiceMessage
import core.controller.component.controllerImpl.Controller
import model.GameState
import model.Move
import model.fieldComponent.fieldImpl.Field
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import util.Event
import util.Observer

import scala.annotation.meta.field
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.*
import scala.io.StdIn
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.concurrent.Promise
import java.util.concurrent.TimeoutException
import core.controller.component.UpdateFieldMessage

class ControllerService(using
    httpService: HttpService
) extends BackendServiceInterface {
  private val persistenceServiceEndpoint = "http://localhost:9021/persistence"
  var controller: ControllerInterface = _
  var queues: List[SourceQueueWithComplete[Message]] = List()
  handleControllerUpdates
  val route: Route =
    concat(
      get {
        pathSingleSlash {
          complete("HearthstoneMini ControllerAPI Service is online.")
        }
      },
      get {
        path("controller" / Segment) { command =>
          command match {
            case "ws"     => handleWebSocketMessages(websocketChanges)
            case "delete" =>
              // DeleteMessage()
              delete match {
                case Success(_) => completeWithData("success")
                case Failure(exception) =>
                  failWith(exception)
              }
            case "gameState" =>
              // GetFieldMessage()
              completeWithData(controller.field.gameState.toString())
            case "field" => {
              var res = Await.result(
                sendRequestToInputA(
                  GetFieldMessage(id = generateRandomMessageId()),
                  2.seconds
                ),
                2.seconds
              )
              completeWithData(res.data.get.toString)
            }
            // completeWithData(controller.field.toJson.toString())
            case "save" =>
              save match {
                case Success(_) => completeWithData("success")
                case Failure(exception) =>
                  failWith(exception)
              }
            case "load" =>
              load match {
                case Success(json) =>
                  // UpdateFieldMessage(json)
                  controller.field = Field.fromJson(json)
                  completeWithData(controller.field.toJson.toString)
                case Failure(exception) =>
                  failWith(exception)
              }
            case "drawCard" =>
              controller.drawCard()
              // DrawCardMessage()
              // UpdateFieldMessage(controller.field.toJson)
              completeWithData(controller.field.toJson.toString)
            case "switchPlayer" =>
              // SwitchPlayerMessage()
              // UpdateFieldMessage(controller.field.toJson)
              controller.switchPlayer()
              completeWithData(controller.field.toJson.toString)
            case "canUndo" =>
              // GetCanUndoMessage()
              // CanUndoResponeMessage()
              complete(controller.canUndo.toString())
            case "canRedo" =>
              // GetCanRedoMessage()
              // CanRedoResponeMessage()
              complete(controller.canRedo.toString())
            case "undo" =>
              // UndoMessage()
              // UpdateFieldMessage(controller.field.toJson)
              controller.undo
              completeWithData(controller.field.toJson.toString)
            case "redo" =>
              // RedoMessage()
              // UpdateFieldMessage(controller.field.toJson)
              controller.redo
              completeWithData(controller.field.toJson.toString)
            case "exitGame" =>
              controller.exitGame()
              completeWithData(controller.field.toJson.toString)
            case _ => failWith(new Exception("Invalid command"))
          }
        }
      },
      post {
        path("controller" / Segment) { command =>
          implicit val jsValueUnmarshaller: Unmarshaller[HttpEntity, JsValue] =
            Unmarshaller.byteStringUnmarshaller.mapWithCharset {
              (data, charset) =>
                Json.parse(data.decodeString(charset.nioCharset.name))
            }

          entity(as[JsValue]) { jsValue =>
            command match {
              case "placeCard" =>
                // PlaceCardMessage(Move.fromJson(jsValue))
                // UpdateFieldMessage(controller.field.toJson)
                controller.placeCard(Move.fromJson(jsValue))
              case "setPlayerNames" =>
                // SetPlayerNamesMessage()
                // UpdateFieldMessage(controller.field.toJson)
                controller.setPlayerNames(
                  (jsValue \ "playername1").as[String],
                  (jsValue \ "playername2").as[String]
                )
              case "setGameState" =>
                // SetGameStateMessage()
                // UpdateFieldMessage(controller.field.toJson)
                controller.setGameState(
                  GameState.withName(
                    jsValue("gameState").toString.replace("\"", "")
                  )
                )
              case "attack" =>
                // AttackMessage()
                // UpdateFieldMessage(controller.field.toJson)
                controller.attack(Move.fromJson(jsValue))
              case "directAttack" =>
                // DirectAttackMessage()
                // UpdateFieldMessage(controller.field.toJson)
                controller.directAttack(Move.fromJson(jsValue))
              case "setStrategy" => {
                // SetStrategyMessage()
                // UpdateFieldMessage(controller.field.toJson)
                controller.setStrategy(
                  Strategy.withName(
                    jsValue("strategy").toString.replace("\"", "")
                  )
                )
              }
              case _ => failWith(new Exception("Invalid command"))
            }

            completeWithData(controller.field.toJson.toString)
          }
        }
      }
    )

  private def failWith(exception: Throwable): StandardRoute = {
    complete(status = 500, exception.getMessage)
  }

  private def completeWithData(data: String): StandardRoute = {
    queues.foreach(_.offer(TextMessage(data)))
    complete(
      HttpEntity(
        ContentTypes.`application/json`,
        data
      )
    )
  }

  def save: Try[Unit] = {
    httpService
      .request(
        persistenceServiceEndpoint,
        "save",
        method = HttpMethods.POST,
        data = Some(controller.field.toJson)
      )
      .map(_ => ())
  }

  def load: Try[JsValue] = {
    httpService.request(
      persistenceServiceEndpoint,
      "load",
      method = HttpMethods.GET
    )
  }

  def delete: Try[Unit] = httpService
    .request(
      persistenceServiceEndpoint,
      "delete",
      method = HttpMethods.GET
    )
    .map(_ => ())

  def handleControllerUpdates: Unit = outputB.runForeach(msg => {
    msg match {
      case UpdateFieldMessage(data, _) =>
        httpService.request(
          persistenceServiceEndpoint,
          "update",
          method = HttpMethods.POST,
          data = data
        )
      case _ =>
    }
  })

  def start(): Unit = {
    val binding = Http().newServerAt("0.0.0.0", 9031).bind(route)

    binding.onComplete {
      case Success(serverBinding) =>
        complete(status = 200, serverBinding.toString())
      case Failure(ex) =>
        complete(status = 500, ex.getMessage)
    }
  }

  def stop(): Unit = {
    system.terminate()
  }
  private val x = websocketChanges

  private def websocketChanges: Flow[Message, Message, Any] =
    val incomingMessages: Sink[Message, Any] =
      Sink.foreach {
        case message: TextMessage.Strict =>
        case _                           =>
      }
    val outgoingMessages: Source[Message, SourceQueueWithComplete[Message]] =
      Source.queue(10, OverflowStrategy.fail).mapMaterializedValue { queue =>
        queues = queue :: queues
        queue
      }
    Flow.fromSinkAndSource(incomingMessages, outgoingMessages)
}
