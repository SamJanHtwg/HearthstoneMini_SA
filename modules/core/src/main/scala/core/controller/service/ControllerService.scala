package core
package controller.service

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl._
import akka.util.ByteString
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, ContentTypes}
import akka.http.scaladsl.server.Directives._
import scala.io.StdIn

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Route
import scala.util.{Failure, Success}
import play.api.libs.json.Json
import core.controller.component.ControllerInterface
import akka.http.scaladsl.unmarshalling.Unmarshaller
import play.api.libs.json.JsValue
import akka.http.scaladsl.unmarshalling.Unmarshal
import model.Move
import core.controller.Strategy.*
import core.controller.Strategy
import model.GameState
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import scala.concurrent.Await
import scala.concurrent.duration.*
import scala.util.Try
import model.fieldComponent.fieldImpl.Field
import scala.annotation.meta.field
import akka.http.scaladsl.server.StandardRoute
import akka.actor.ActorRef
import akka.actor.Actor
import akka.http.scaladsl.model.ws.TextMessage
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Flow
import akka.stream.SourceShape
import akka.http.scaladsl.model.ws.Message
import core.util.Observer
import core.util.Event

class ControllerService(using controller: ControllerInterface) {
  private val persistenceServiceEndpoint = "http://localhost:9021/persistence"

  implicit val system: ActorSystem[String] =
    ActorSystem(Behaviors.empty, "SprayExample")
  implicit val executionContext: ExecutionContext = system.executionContext
  object UpdateObserver extends Observer {

    override def update(e: Event, msg: Option[String]): Unit = {
      val updateRequest = Http().singleRequest(
        HttpRequest(
          uri = s"$persistenceServiceEndpoint/update",
          method = HttpMethods.POST,
          entity = HttpEntity(
            ContentTypes.`application/json`,
            controller.field.toJson.toString
          )
        )
      )

      val responseJsonFuture = updateRequest.flatMap { response =>
        Unmarshal(response.entity).to[String].map { jsonString =>
          Json.parse(jsonString)
        }
      }

      Try {
        Await.result(responseJsonFuture, 300.seconds)
      }.map(_ => ())
    }

  }
  controller.add(UpdateObserver)
  var queues: List[SourceQueueWithComplete[Message]] = List()
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
            case "ws" => handleWebSocketMessages(websocketChanges)
            case "delete" => delete match {
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
                  complete(
                    status = 500,
                    Json.prettyPrint(
                      Json.obj("error" -> Json.parse(exception.getMessage))
                    )
                  )
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
    val saveRequest = Http().singleRequest(
      HttpRequest(
        uri = s"$persistenceServiceEndpoint/save",
        method = HttpMethods.POST,
        entity = HttpEntity(
          ContentTypes.`application/json`,
          controller.field.toJson.toString
        )
      )
    )

    val responseJsonFuture = saveRequest.flatMap { response =>
      Unmarshal(response.entity).to[String].map { jsonString =>
        Json.parse(jsonString)
      }
    }

    Try {
      Await.result(responseJsonFuture, 3.seconds)
    }.map(_ => ())
  }

  def load: Try[JsValue] = {
    val loadRequest = Http().singleRequest(
      HttpRequest(
        uri = s"$persistenceServiceEndpoint/load",
        method = HttpMethods.GET
      )
    )

    val responseJsonFuture = loadRequest.flatMap { response =>
      Unmarshal(response.entity).to[String].map { jsonString =>
        Json.parse(jsonString)
      }
    }

    Try {
      Await.result(responseJsonFuture, 3.seconds)
    }
  }
  
  def delete: Try[Unit] = {
    val loadRequest = Http().singleRequest(
      HttpRequest(
        uri = s"$persistenceServiceEndpoint/delete",
        method = HttpMethods.GET
      )
    )

    Try[Unit] {
      Await.result(loadRequest, 3.seconds)
    }
  }

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
