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
import model.fieldComponent.fieldImpl.Field
import persistence.database.DaoInterface
import persistence.database.slick.SlickDatabase
import persistence.database.mongodb.MongoDatabase

class PersistenceService(
    fileIO: FileIOInterface = JsonIO(),
    dao: DaoInterface = MongoDatabase
) {
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
            dao.save(Field.fromJson(json))
            complete("Saved")
          }
        }
      },
      post {
        path("persistence" / "update") {
          entity(as[String]) { updateRequest =>
            val json = Json.parse(updateRequest)
            dao.update(Field.fromJson(json))
            complete("Updated")
          }
        }
      },
      get {
        path("persistence" / "load") {
          dao.load() match {
            case Success(field) =>
              complete(status = 200, Json.prettyPrint(field))
            case Failure(error) =>
              println(error)
              complete(status = 500, error.getMessage)
          }
          // fileIO.load() match {
          //   case Success(field) =>
          //     complete(Json.prettyPrint(field.toJson))
          //   case Failure(exception) =>
          //     complete(status = 500, exception.getMessage)
          // }
        }
      },
      get {
        path("persistence" / "delete") {
          dao.delete() match
            case Success(_) =>
              complete(status = 200, "deleted")
            case Failure(error) =>
              complete(status = 500, error.getMessage)
        }
      },
      post {
        path("persistence" / "stopServer") {
          onComplete(stop()) {
            case Success(_) =>
              complete("Server stopped")
            case Failure(ex) =>
              complete(StatusCodes.InternalServerError, ex.getMessage)
          }
        }
      }
    )

  def start(): Unit = {
    bindingFuture = Http().newServerAt("0.0.0.0", 9021).bind(route)

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
