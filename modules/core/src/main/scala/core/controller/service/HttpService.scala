package core.controller.service

import akka.http.scaladsl.model.HttpMethod
import scala.util.Try
import play.api.libs.json.JsValue
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpEntity
import play.api.libs.json.Json
import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.concurrent.Await
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.model.ws.WebSocketRequest
import akka.stream.scaladsl.*
import akka.http.scaladsl.model.ws.*
import akka.stream.OverflowStrategy
import akka.http.scaladsl.model.StatusCodes
import scala.concurrent.Future

class HttpService {
  implicit val system: ActorSystem[?] =
    ActorSystem(Behaviors.empty, "SingleRequest")
  implicit val executionContext: ExecutionContext = system.executionContext

  def connectToWebSocket(
      url: String,
      onEvent: Message => Unit
  ): Future[Unit] = {
    val (webSocketUpgradeResponse, webSocketOut) =
      Http().singleWebSocketRequest(
        WebSocketRequest(uri = url),
        Flow.fromSinkAndSourceMat(
          Sink.foreach[Message] { onEvent },
          Source
            .actorRef[TextMessage](bufferSize = 10, OverflowStrategy.fail)
            .mapMaterializedValue { webSocketIn =>
              webSocketIn
            }
        )(Keep.both)
      )

    webSocketUpgradeResponse.flatMap { upgrade =>
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        system.log.info("WebSocket connection established")
        println("WebSocket connection established")
        Future.successful(())
      } else {
        println(s"WebSocket connection failed: ${upgrade.response.status}")
        throw new RuntimeException(
          s"WebSocket connection failed: ${upgrade.response.status}"
        )
      }
    }
  }

  def request(
      endPoint: String,
      command: String,
      method: HttpMethod,
      data: Option[JsValue] = None
  ): Try[JsValue] = Try {
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
        Unmarshal(response.entity).to[String].flatMap { entityString =>
          if (response.status.isSuccess()) {
            Future.successful(Json.parse(entityString))
          } else {
            Future.failed(new RuntimeException(entityString))
          }
        }
      }

      Await.result(responseJsonFuture, 3.seconds)
    }
}
