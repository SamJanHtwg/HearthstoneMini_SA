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

class HttpService {
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
}
