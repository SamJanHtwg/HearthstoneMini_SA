package core.controller.service

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

class ControllerRestService(using controller: ControllerInterface) {

  implicit val system: ActorSystem[?] =
    ActorSystem(Behaviors.empty, "SprayExample")
  implicit val executionContext: ExecutionContext = system.executionContext

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
            case "gameState" =>
              complete(
                HttpEntity(
                  ContentTypes.`application/json`,
                  controller.field.gameState.toString()
                )
              )
            case "drawCard" =>
              controller.drawCard()
            case "switchPlayer" =>
              controller.switchPlayer()
            case "undo" =>
              controller.undo
            case "redo" =>
              controller.redo
            case "exitGame" =>
              controller.exitGame()
            case _ => failWith(new Exception("Invalid command"))
          }

          complete(
            HttpEntity(
              ContentTypes.`application/json`,
              Json.stringify(controller.field.toJson)
            )
          )
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
              case "attack" =>
                controller.attack(Move.fromJson(jsValue))
              case "directAttack" =>
                controller.directAttack(Move.fromJson(jsValue))
              case "setStrategy" => {
                controller.setStrategy(
                  Strategy.withName(jsValue("strategy").toString.replace("\"", ""))
                )
              }
              case _ => failWith(new Exception("Invalid command"))
            }

            complete(
              HttpEntity(
                ContentTypes.`application/json`,
                Json.stringify(controller.field.toJson)
              )
            )
          }
        }
      }
    )

  def start(): Unit = {
    val binding = Http().newServerAt("localhost", 4001).bind(route)

    binding.onComplete {
      case Success(binding) =>
        println(
          s"HearthstoneMini ControllerAPI service online at http://localhost:4001/"
        )
      case Failure(exception) =>
        println(
          s"HearthstoneMini ControllerAPI service failed to start: ${exception.getMessage}"
        )
    }
  }

}
