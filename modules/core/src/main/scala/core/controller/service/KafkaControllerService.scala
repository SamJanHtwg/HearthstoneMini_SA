package core.controller.component

import akka.actor.typed.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.Materializer
import akka.util.ByteString
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{
  ByteArrayDeserializer,
  ByteArraySerializer,
  StringDeserializer,
  StringSerializer
}
import play.api.libs.json.{JsValue, Json}
import core.controller._
import scala.concurrent.Future

class KafkaControllerService extends ControllerServiceInterface {
  private val kafkaBootstrapServers = "localhost:9092"
  private val consumerTopic = "service-messages"
  private val producerTopic = "game-updates"

  private val producerSettings =
    ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(kafkaBootstrapServers)  

  private val consumerSettings =
    ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers(kafkaBootstrapServers)
      .withGroupId("group1")



  override def start(): Unit = {
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(consumerTopic))
      .map { record =>
        Json.parse(record.value()).as[ServiceMessage]
      }
      .runWith(inputA)

    outputB
      .map { msg =>
        val json = Json.stringify(Json.toJson(msg))
        new ProducerRecord[Array[Byte], String](producerTopic, json)
      }
      .runWith(Producer.plainSink(producerSettings))
  }
}
