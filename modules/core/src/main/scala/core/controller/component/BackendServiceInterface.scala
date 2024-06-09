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
import akka.actor.Actor
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

trait BackendServiceInterface {
  implicit val system: ActorSystem[ServiceMessage] =
    ActorSystem(Behaviors.empty, "BackendService")
  implicit val materializer: Materializer = Materializer(system)
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

  def start(): Unit;
}

sealed trait ServiceMessage {
  val data: Option[JsValue]
}

case class GetFieldMessage(data: Option[JsValue] = None) extends ServiceMessage
case class UpdateFieldMessage(data: Option[JsValue]) extends ServiceMessage
case class DeleteFieldMessage(data: Option[JsValue]) extends ServiceMessage



