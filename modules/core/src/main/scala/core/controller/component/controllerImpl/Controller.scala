package core.controller.component.controllerImpl

import com.google.inject.name.{Named, Names}
import com.google.inject.{Guice, Inject, Injector}
import model.GameState.*
import core.controller.component.ControllerInterface
import core.controller.Strategy.Strategy
import core.controller.Strategy
import model.GameState
import model.fieldComponent.FieldInterface
import model.Move
import persistence.fileIO.FileIOInterface
import model.playerComponent.playerImpl.Player
import net.codingwell.scalaguice.InjectorExtensions.*
import core.util.UndoManager
import util.{Observer, Observable, Event}
import core.util.commands.CommandInterface
import model.fieldComponent.fieldImpl.Field
import core.controller._
import java.lang.System.exit
import java.text.Annotation
import scala.util.{Failure, Success, Try}
import core.util.commands.commandImpl.{
  PlaceCardCommand,
  DirectAttackCommand,
  DrawCardCommand,
  SwitchPlayerCommand,
  AttackCommand
}
import core.util.CardProvider
import core.controller.component.ControllerServiceInterface
import akka.stream.scaladsl.Sink
import org.reactivestreams.Subscriber
import akka.stream.scaladsl.Source
import akka.actor.Actor
import core.controller.component.*
import play.api.libs.json.JsValue
import play.api.libs.json.Json

class Controller(
    val fileIO: FileIOInterface,
    private val undoManager: UndoManager,
    private val cardProvider: CardProvider,
    private val controllerService: ControllerServiceInterface
) extends ControllerInterface {
  field = Field(
    players = Map(
      1 -> Player(
        id = 1,
        hand = cardProvider.getCards(5),
        deck = cardProvider.getCards(30)
      ),
      2 -> Player(
        id = 2,
        hand = cardProvider.getCards(5),
        deck = cardProvider.getCards(30)
      )
    )
  )
  subcribeToServiceRequests

  def canUndo: Boolean = undoManager.canUndo()
  def canRedo: Boolean = undoManager.canRedo()

  def placeCard(move: Move): Unit = doStep(new PlaceCardCommand(field, move))
  def drawCard(): Unit = doStep(new DrawCardCommand(field))
  def setPlayerNames(playername1: String, playername2: String): Unit = {
    field = field.setPlayerNames(playername1, playername2)
    field = field.setGameState(GameState.MAINGAME)
    notifyObservers(Event.PLAY, msg = None)
  }
  def attack(move: Move): Unit = doStep(new AttackCommand(field, move))
  def directAttack(move: Move): Unit = doStep(
    new DirectAttackCommand(field, move)
  )
  def switchPlayer(): Unit = doStep(new SwitchPlayerCommand(field))

  def doStep(command: CommandInterface): Unit = {
    command.doStep match {
      case Success(newField) => 
        {
          field = newField
          undoManager.doStep(command)
          errorMsg = None
          notifyObservers(Event.PLAY, msg = errorMsg)
        }
      case Failure(x) =>
        errorMsg = Some(x.getMessage)
        notifyObservers(Event.ERROR, msg = errorMsg)
    }
  }

  def undo: Unit = {
    undoManager.undoStep(field) match
      case Failure(exception) =>
        errorMsg = Some(exception.getMessage)
        notifyObservers(Event.ERROR, msg = errorMsg)
      case Success(value) =>
        field = value
        errorMsg = None
        notifyObservers(Event.PLAY, msg = None)
  }

  def redo: Unit = {
    undoManager.redoStep(field) match
      case Failure(exception) =>
        errorMsg = Some(exception.getMessage)
        notifyObservers(Event.ERROR, msg = errorMsg)
      case Success(value) =>
        field = value
        errorMsg = None
        notifyObservers(Event.PLAY, msg = None)
  }
  def deleteField: Unit = {}
  def exitGame(): Unit = {
    errorMsg = None
    setGameState(GameState.EXIT)
  }

  def setStrategy(strat: Strategy): Unit = {
    field = strat match {
      case Strategy.normal   => field.setHpValues(30).setManaValues(1)
      case Strategy.hardcore => field.setHpValues(10).setManaValues(10)
      case Strategy.debug    => field.setHpValues(100).setManaValues(100)
    }
    field = field.setGameState(GameState.ENTERPLAYERNAMES)
    errorMsg = None
    notifyObservers(Event.PLAY, msg = None)
  }

  def getWinner(): Option[String] = {
    val playersWithHp = field.players.filterNot(_._2.isHpEmpty)
    playersWithHp.values.size match {
      case 1 =>
        field.setGameState(GameState.WIN)
        Some(playersWithHp.values.head.name)
      case _ => None
    }
  }
  def setGameState(gameState: GameState): Unit = {
    field = field.setGameState(gameState)
    notifyObservers(Event.PLAY, msg = None)
  }
  def saveField: Unit = {
    fileIO.save(this.field)
  }
  def loadField: Unit = {
    fileIO.load() match {
      case Success(value) =>
        this.field = value
        errorMsg = None
        setGameState(GameState.MAINGAME)
      case Failure(exception) =>
        errorMsg = Some("Sieht so aus als wäre die Datei beschädigt.")
        notifyObservers(Event.ERROR, msg = errorMsg)
    }
  }

  private def subcribeToServiceRequests =
    controllerService
      .outputA()
      .runWith(Sink.foreach(handleServiceMessage))
      (controllerService.materializer)

  def handleServiceMessage(msg: ServiceMessage) = msg match {
    case GetFieldMessage(data, id) =>
      controllerService.sendMessageToInputB(
        UpdateFieldMessage(Some(field.toJson), id = msg.id)
      )
    case UpdateFieldMessage(Some(jsValue), id) =>
      field = Field.fromJson(jsValue).setGameState(GameState.MAINGAME)

      controllerService.sendMessageToInputB(
        UpdateFieldMessage(Some(field.toJson), id)
      )
      notifyObservers(Event.PLAY, msg = None)
    case SetStrategyMessage(data, id) =>
      setStrategy(
        Strategy.withName(
          data.get("strategy").toString.replace("\"", "")
        )
      )
      controllerService.sendMessageToInputB(
        UpdateFieldMessage(Some(field.toJson), id = msg.id)
      )
    case SetPlayerNamesMessage(data, id) =>
      setPlayerNames(
        data.get("playername1").toString.replace("\"", ""),
        data.get("playername2").toString.replace("\"", "")
      )
      controllerService.sendMessageToInputB(
        UpdateFieldMessage(Some(field.toJson), id = msg.id)
      )
    case DrawCardMessage(data, id) =>
      drawCard()
      controllerService.sendMessageToInputB(
        UpdateFieldMessage(Some(field.toJson), id = msg.id)
      )
    case SwitchPlayerMessage(data, id) =>
      switchPlayer()
      controllerService.sendMessageToInputB(
        UpdateFieldMessage(Some(field.toJson), id = msg.id)
      )
    case CanUndoMessage(data, id) =>
      controllerService.sendMessageToInputB(
        CanUndoResponeMessage(Some(Json.toJson(canUndo)), id = msg.id)
      )
    case CanRedoMessage(data, id) =>
      controllerService.sendMessageToInputB(
        CanRedoResponeMessage(Some(Json.toJson(canRedo)), id = msg.id)
      )
    case UndoMessage(data, id) =>
      undo
      controllerService.sendMessageToInputB(
        UpdateFieldMessage(Some(field.toJson), id = msg.id)
      )
    case RedoMessage(data, id) =>
      redo
      controllerService.sendMessageToInputB(
        UpdateFieldMessage(Some(field.toJson), id = msg.id)
      )
    case PlaceCardMessage(data, id) =>
      placeCard(Move.fromJson(data.get))
      controllerService.sendMessageToInputB(
        UpdateFieldMessage(Some(field.toJson), id = msg.id)
      )
    case SetGameStateMessage(data, id) =>
      setGameState(
        GameState.withName(data.get("gamestate").toString.replace("\"", ""))
      )
      controllerService.sendMessageToInputB(
        UpdateFieldMessage(Some(field.toJson), id = msg.id)
      )
    case AttackMessage(data, id) =>
      attack(Move.fromJson(data.get))
      controllerService.sendMessageToInputB(
        UpdateFieldMessage(Some(field.toJson), id = msg.id)
      )
    case DirectAttackMessage(data, id) =>
      directAttack(Move.fromJson(data.get))
      controllerService.sendMessageToInputB(
        UpdateFieldMessage(Some(field.toJson), id = msg.id)
      )
    case _ => println("Unknown message type: " + msg.getClass.getSimpleName)
  }
}
