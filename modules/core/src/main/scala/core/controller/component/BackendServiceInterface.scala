package core.controller.component

import akka.stream.scaladsl.Source
import play.api.libs.json.JsValue
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.Sink
import akka.stream.Materializer
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl.SourceQueueWithComplete
import akka.stream.OverflowStrategy
import akka.NotUsed
import akka.actor.typed.Props
import akka.util.ByteString
import java.nio.ByteOrder
import akka.actor.Props
import akka.stream.scaladsl.BidiFlow
import akka.stream.scaladsl.GraphDSL
import akka.stream.BidiShape
import akka.stream.scaladsl.Flow
import org.reactivestreams.Subscriber
import org.reactivestreams.Publisher
import core.controller.component.controllerImpl.Controller
import akka.Done
import scala.concurrent.Future
import akka.stream.scaladsl.BroadcastHub
import akka.stream.scaladsl.Keep
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.*
import scala.concurrent.duration.*
import akka.pattern.after
import scala.util.Try

trait BackendServiceInterface {
  implicit val system: ActorSystem[ServiceMessage] =
    ActorSystem(Behaviors.empty, "BackendService")
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContext = system.executionContext
  val bufferSize = 256

  // Source and Sink A
  private val (sinkA, sourceA) =
    Source
      .queue[ServiceMessage](bufferSize, OverflowStrategy.dropHead)
      .toMat(BroadcastHub.sink(bufferSize))(Keep.both)
      .run()

  // Source and Sink B
  private val (sinkB, sourceB) =
    Source
      .queue[ServiceMessage](bufferSize, OverflowStrategy.dropHead)
      .toMat(BroadcastHub.sink(bufferSize))(Keep.both)
      .run()

  def inputA: Sink[ServiceMessage, Future[Done]] = Sink.foreach[ServiceMessage] {
    msg =>
      sinkA.offer(msg)
  }

  def outputA: Source[ServiceMessage, NotUsed] = sourceA

  def inputB: Sink[ServiceMessage, Future[Done]] = Sink.foreach[ServiceMessage] {
    msg =>
      sinkB.offer(msg)
  }

  def outputB: Source[ServiceMessage, NotUsed] = sourceB

  def sendMessageToInputB(msg: ServiceMessage): Unit = {
    Source.single(msg).runWith(inputB)(materializer)
  }
  def sendRequestToInputA(
      request: ServiceMessage,
      timeout: FiniteDuration = 2.seconds
  ): Try[ServiceMessage] = {
    val promise = Promise[ServiceMessage]()

    // Subscribe to outputB and wait for the matching response
    val cancellable = outputB.runForeach {
      case msg if msg.id == request.id => promise.success(msg)
      case cause                       => promise.failure(Throwable(cause.toString()))// Ignore other messages
    }

    // Send the request
    inputA.runWith(Source.single(request))

    val timeoutFuture =
      after(timeout)(Future.failed(new TimeoutException("Request timed out")))

    // Return the future that completes first: either the promise when a response is received, or the timeoutFuture when the timeout is reached
    Try(Await.result(Future.firstCompletedOf(Seq(promise.future, timeoutFuture)), timeout))
  }
  
  def generateRandomMessageId(): String = java.util.UUID.randomUUID().toString
  
  def start(): Unit;
}

sealed trait ServiceMessage {
  val id: String
  val data: Option[JsValue]
}

case class GetFieldMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class UpdateFieldMessage(data: Option[JsValue], id: String) extends ServiceMessage
case class DeleteMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class DrawCardMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class SwitchPlayerMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class CanUndoMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class CanUndoResponeMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class CanRedoMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class CanRedoResponeMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class UndoMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class RedoMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class PlaceCardMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class SetPlayerNamesMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class SetGameStateMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class SetStrategyMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class AttackMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class DirectAttackMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage
case class EndTurnMessage(data: Option[JsValue] = None, id: String) extends ServiceMessage



