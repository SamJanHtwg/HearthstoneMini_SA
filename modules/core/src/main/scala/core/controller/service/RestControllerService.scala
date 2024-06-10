package core.controller.service

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
import core.controller.component.*
import core.controller.component.ControllerServiceInterface
import model.fieldComponent.fieldImpl.Field
import play.api.libs.json.JsValue
import play.api.libs.json.Json

import scala.concurrent.duration.*
import scala.util.*

class RestBackendService(using
    httpService: HttpService
) extends ControllerServiceInterface {
  private val persistenceServiceEndpoint = "http://localhost:9021/persistence"
  private var queues: List[SourceQueueWithComplete[Message]] = List()
  
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
              // TODO: Check if needed (i dont think so)
              // DeleteMessage()
              delete match {
                case Success(_) => completeWithData("success")
                case Failure(exception) =>
                  failWith(exception)
              }
            case "gameState" =>
              sendRequestToInputA(
                GetFieldMessage(id = generateRandomMessageId()),
                2.seconds
              ) match
                case Success(message) =>
                  completeWithData(
                    message.data.map(Field.fromJson(_)).get.gameState.toString
                  )
                case Failure(exception) => failWith(exception)
            case "field" => {
              sendRequestToInputA(
                GetFieldMessage(id = generateRandomMessageId())
              ) match {
                case Success(message) =>
                  completeWithData(message.data.get.toString)
                case Failure(exception) => failWith(exception)
              }
            }
            case "save" =>
              save match {
                case Success(_) => completeWithData("success")
                case Failure(exception) =>
                  failWith(exception)
              }
            case "load" =>
              load match {
                case Success(json) =>
                  sendRequestToInputA(
                    UpdateFieldMessage(
                      Some(json),
                      id = generateRandomMessageId()
                    )
                  ) match {
                    case Success(message) =>
                      completeWithData(message.data.get.toString)
                    case Failure(exception) => failWith(exception)
                  }
                case Failure(exception) =>
                  failWith(exception)
              }
            case "drawCard" =>
              sendRequestToInputA(
                DrawCardMessage(id = generateRandomMessageId())
              ) match {
                case Success(message) =>
                  completeWithData(message.data.get.toString)
                case Failure(exception) => failWith(exception)
              }
            case "switchPlayer" =>
              sendRequestToInputA(
                SwitchPlayerMessage(id = generateRandomMessageId())
              ) match {
                case Success(message) =>
                  completeWithData(message.data.get.toString)
                case Failure(exception) => failWith(exception)
              }
            case "canUndo" =>
              sendRequestToInputA(
                CanUndoMessage(id = generateRandomMessageId())
              ) match {
                case Success(message)   => complete(message.data.get.toString)
                case Failure(exception) => failWith(exception)
              }
            case "canRedo" =>
              sendRequestToInputA(
                CanRedoMessage(id = generateRandomMessageId())
              ) match {
                case Success(message)   => complete(message.data.get.toString)
                case Failure(exception) => failWith(exception)
              }
            case "undo" =>
              sendRequestToInputA(
                UndoMessage(id = generateRandomMessageId())
              ) match {
                case Success(message) =>
                  completeWithData(message.data.get.toString)
                case Failure(exception) => failWith(exception)
              }
            case "redo" =>
              sendRequestToInputA(
                RedoMessage(id = generateRandomMessageId())
              ) match {
                case Success(message) =>
                  completeWithData(message.data.get.toString)
                case Failure(exception) => failWith(exception)
              }
            case "exitGame" =>
              completeWithData("Exited")
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
                sendRequestToInputA(
                  PlaceCardMessage(
                    Some(jsValue),
                    id = generateRandomMessageId()
                  )
                ) match {
                  case Success(message) =>
                    completeWithData(message.data.get.toString)
                  case Failure(exception) => failWith(exception)
                }
              case "setPlayerNames" =>
                sendRequestToInputA(
                  SetPlayerNamesMessage(
                    Some(jsValue),
                    id = generateRandomMessageId()
                  )
                ) match {
                  case Success(message) =>
                    completeWithData(message.data.get.toString)
                  case Failure(exception) => failWith(exception)
                }
              case "setGameState" =>
                sendRequestToInputA(
                  SetGameStateMessage(
                    Some(jsValue),
                    id = generateRandomMessageId()
                  )
                ) match {
                  case Success(message) =>
                    completeWithData(message.data.get.toString)
                  case Failure(exception) => failWith(exception)
                }
              case "attack" =>
                sendRequestToInputA(
                  AttackMessage(Some(jsValue), id = generateRandomMessageId())
                ) match {
                  case Success(message) =>
                    completeWithData(message.data.get.toString)
                  case Failure(exception) => failWith(exception)
                }
              case "directAttack" =>
                sendRequestToInputA(
                  DirectAttackMessage(
                    Some(jsValue),
                    id = generateRandomMessageId()
                  )
                ) match {
                  case Success(message) =>
                    completeWithData(message.data.get.toString)
                  case Failure(exception) => failWith(exception)
                }
              case "setStrategy" => {
                sendRequestToInputA(
                  SetStrategyMessage(
                    Some(jsValue),
                    id = generateRandomMessageId()
                  )
                ) match {
                  case Success(message) =>
                    completeWithData(message.data.get.toString)
                  case Failure(exception) => failWith(exception)
                }
              }
              case _ => failWith(new Exception("Invalid command"))
            }
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
    sendRequestToInputA(
      GetFieldMessage(id = generateRandomMessageId())
    ) match {
      case Success(message) =>
        httpService
          .request(
            persistenceServiceEndpoint,
            "save",
            method = HttpMethods.POST,
            data = message.data
          )
          .map(_ => ())
      case Failure(exception) =>
        Failure(exception)
    }

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
