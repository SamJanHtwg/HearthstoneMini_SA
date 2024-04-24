package persistence
package fileIO.service

import fileIO.FileIOInterface
import fileIO.jsonIOImpl.JsonIO

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
import akka.protobufv3.internal.compiler.PluginProtos.CodeGeneratorResponse.File
import org.checkerframework.checker.units.qual.s
import akka.compat.Future
import scala.concurrent.Future
import akka.http.scaladsl.model.StatusCodes
import akka.Done

class PersistenceService(fileIO: FileIOInterface = JsonIO()) {
  implicit val system: ActorSystem[?] =
    ActorSystem(Behaviors.empty, "SingleRequest")
  implicit val executionContext: ExecutionContext = system.executionContext

  var bindingFuture: Future[Http.ServerBinding] = _

  val route: Route =
    concat(
      get {
        pathSingleSlash {
          complete("Persistence Service")
        }
      },
      post {
        path("persistence" / "save") {
          entity(as[String]) { saveRequest =>
            val json = Json.parse(saveRequest)
            fileIO.save(json)
            complete("Saved")
          }
        }
      },
      get {
        path("persistence" / "load") {
          fileIO.load() match {
            case Success(field) =>
              complete(Json.prettyPrint(field))
            case Failure(exception) =>
              complete(status = 500, exception.getMessage)
          }
        }
      },
      post {
        path("persistence" / "stopServer") {
          onComplete(stop()) {
            case Success(_) =>
              complete("Server stopped")
            case Failure(ex) =>
              complete(
                StatusCodes.InternalServerError,
                ex.getMessage)
          }
        }
      }
    )

  def start(): Unit = {
    bindingFuture = Http().newServerAt("localhost", 5001).bind(route)

    bindingFuture.onComplete({
      case Success(serverBinding) =>
        complete(status = 200, serverBinding.toString())
      case Failure(ex) =>
        complete(status = 500, ex.getMessage)
    })
  }

  def stop(): Future[Done] = {
    bindingFuture.flatMap(_.unbind())
  }
}
