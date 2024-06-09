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
/*
  Remove the controlelr dependency from the ControllerService
  controller wants an backend service interface
  Rest service should be a backend service
  Kafka service should be a backend service
  service and controlelr communicate through streams and messages
 */

class ControllerService(using
    httpService: HttpService
) extends BackendServiceInterface {
  // TODO: Handle incomming ServiceMessages from controller
  outputB.runForeach(println)

  private val persistenceServiceEndpoint = "http://localhost:9021/persistence"
  implicit val executionContext: ExecutionContext = system.executionContext
  // object UpdateObserver extends Observer {

  //   override def update(e: Event, msg: Option[String]): Unit = {
  //     httpService.request(
  //       persistenceServiceEndpoint,
  //       "update",
  //       method = HttpMethods.POST,
  //       data = Some(controller.field.toJson)
  //     )
  //   }
  // }
  // controller.add(UpdateObserver)

  println("init service")
  var controller: ControllerInterface = _

  var queues: List[SourceQueueWithComplete[Message]] = List()
  val route: Route =
    concat(
      get {
        pathSingleSlash {
          Source
            .single(GetFieldMessage())
            .runWith(inputA)
          complete("HearthstoneMini ControllerAPI Service is online.")
        }
      },
      get {
        path("controller" / Segment) { command =>
          command match {
            case "ws" => handleWebSocketMessages(websocketChanges)
            case "delete" =>
              delete match {
                case Success(_) => completeWithData("success")
                case Failure(exception) =>
                  failWith(exception)
              }
            case "gameState" =>
              completeWithData(controller.field.gameState.toString())
            case "field" =>
              completeWithData(controller.field.toJson.toString())
            case "save" =>
              save match {
                case Success(_) => completeWithData("success")
                case Failure(exception) =>
                  failWith(exception)
              }
            case "load" =>
              load match {
                case Success(json) =>
                  controller.field = Field.fromJson(json)
                  completeWithData(controller.field.toJson.toString)
                case Failure(exception) =>
                  failWith(exception)
              }
            case "drawCard" =>
              controller.drawCard()
              completeWithData(controller.field.toJson.toString)
            case "switchPlayer" =>
              controller.switchPlayer()
              completeWithData(controller.field.toJson.toString)
            case "canUndo" =>
              complete(controller.canUndo.toString())
            case "canRedo" =>
              complete(controller.canRedo.toString())
            case "undo" =>
              controller.undo
              completeWithData(controller.field.toJson.toString)
            case "redo" =>
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
                controller.placeCard(Move.fromJson(jsValue))
              case "setPlayerNames" =>
                controller.setPlayerNames(
                  (jsValue \ "playername1").as[String],
                  (jsValue \ "playername2").as[String]
                )
              case "setGameState" =>
                controller.setGameState(
                  GameState.withName(
                    jsValue("gameState").toString.replace("\"", "")
                  )
                )
              case "attack" =>
                controller.attack(Move.fromJson(jsValue))
              case "directAttack" =>
                controller.directAttack(Move.fromJson(jsValue))
              case "setStrategy" => {
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
