package core.controller.component.controllerImpl

import core.controller.component.ControllerInterface
import model.fieldComponent.FieldInterface
import model.GameState.GameState
import model.Move
import core.controller.Strategy.Strategy
import akka.kafka.ConsumerSettings
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import scala.concurrent.ExecutionContext
import akka.kafka.ProducerSettings
import org.apache.kafka.common.serialization._
import org.apache.kafka.clients.consumer.ConsumerConfig
import akka.kafka.scaladsl.Producer
import akka.kafka.scaladsl.Consumer
import akka.kafka.Subscriptions
import akka.stream.scaladsl.Source
import core.controller._
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.UUID
import play.api.libs.json.Json
import model.fieldComponent.fieldImpl.Field
import util.Event

class KafkaControllerClient extends ControllerInterface {
  private val kafkaBootstrapServers = "localhost:9092"
  private val consumerTopic = "game-updates"
  private val producerTopic = "service-messages"

  implicit val system: ActorSystem[?] =
    ActorSystem(Behaviors.empty, "SprayExample")
  implicit val executionContext: ExecutionContext = system.executionContext

  private val producerSettings =
    ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(kafkaBootstrapServers)
      

  private val consumerSettings =
    ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers(kafkaBootstrapServers)
      .withGroupId("group1")

  val sink = Producer.plainSink(producerSettings)
  val source =
    Consumer.plainSource(consumerSettings, Subscriptions.topics(consumerTopic))

  source.runForeach(record => {
    val message = Json.parse(record.value()).as[ServiceMessage]
    message match {
      case UpdateFieldMessage(Some(field), _) =>
        this.field = Field.fromJson(field)
        notifyObservers(Event.PLAY, msg = errorMsg)
      case _ => {}
    }
  })

  override def exitGame(): Unit = {}

  override def placeCard(move: Move): Unit = sendMessageToService(
    PlaceCardMessage(Some(move.toJson), id = UUID.randomUUID().toString())
  )

  override def setPlayerNames(playername1: String, playername2: String): Unit =
    sendMessageToService(
      SetPlayerNamesMessage(
         Some(
          Json.obj(
            "playername1" -> playername1,
            "playername2" -> playername2
          )
        ),
        id = UUID.randomUUID().toString()
      )
    )

  override def switchPlayer(): Unit = sendMessageToService(
    SwitchPlayerMessage(id = UUID.randomUUID().toString())
  )

  override def attack(move: Move): Unit = sendMessageToService(
    AttackMessage(Some(move.toJson), id = UUID.randomUUID().toString())
  )

  override def canRedo: Boolean = true

  override def canUndo: Boolean = true

  override def setStrategy(strat: Strategy): Unit = {
    sendMessageToService(
      SetStrategyMessage(
        Some(Json.obj("strategy" -> strat.toString)),
        id = UUID.randomUUID().toString()
      )
    )
  }

  override def redo: Unit = sendMessageToService(
    RedoMessage(id = UUID.randomUUID().toString())
  )

  override def undo: Unit = sendMessageToService(
    UndoMessage(id = UUID.randomUUID().toString())
  )

  override def loadField: Unit = sendMessageToService(
    GetFieldMessage(id = UUID.randomUUID().toString())
  )

  override def directAttack(move: Move): Unit = sendMessageToService(
    DirectAttackMessage(Some(move.toJson), id = UUID.randomUUID().toString())
  )

  override def saveField: Unit = {}

  override def setGameState(gameState: GameState): Unit = sendMessageToService(
    SetGameStateMessage(
      Some(Json.obj("gameState" -> gameState.toString)),
      id = UUID.randomUUID().toString()
    )
  )

  override def deleteField: Unit = {}

  override def getWinner(): Option[String] = None

  override def drawCard(): Unit = sendMessageToService(
    DrawCardMessage(id = UUID.randomUUID().toString())
  )

  def sendMessageToService(message: ServiceMessage): Unit = {
    val json = Json.toJson(message)
    val record =
      new ProducerRecord[Array[Byte], String](producerTopic, json.toString())
    Source.single(record).runWith(sink)
  }
}
