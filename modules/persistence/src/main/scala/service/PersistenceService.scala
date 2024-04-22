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

class PersistenceService() {
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
            JsonIO().save(json)
            complete("Saved")
          }
        }
      },
      get {
        path("persistence" / "load") {
          JsonIO().load match {
            case Success(field) =>
              complete(Json.prettyPrint(field.toJson))
            case Failure(exception) =>
              failWith(Throwable("Error loading field"))
          }
        }
      },
      post {
        path("persistence" / "stopServer") {
          onComplete(bindingFuture.flatMap(_.unbind())) {
            case Success(_) =>
              system.terminate()
              complete("Server stopped")
            case Failure(ex) =>
              complete(s"Error stopping server: $ex")
          }
        }
      }
    )

  def start(): Unit = {
    bindingFuture = Http().newServerAt("localhost", 5001).bind(route)

    bindingFuture.onComplete({
      case Success(serverBinding) =>
        println(
          s"Server online at http://localhost:5001/"
        )
      case Failure(ex) =>
        println(s"Server could not start: $ex")
        system.terminate()
    })
  }

  def stop(): Unit = {
    bindingFuture.flatMap(_.unbind())
    system.terminate()
  }
}
