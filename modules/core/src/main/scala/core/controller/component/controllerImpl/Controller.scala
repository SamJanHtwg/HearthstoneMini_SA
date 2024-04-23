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
import core.util.{Event, Observable, UndoManager}
import core.util.commands.CommandInterface
import model.fieldComponent.fieldImpl.Field

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

class Controller(
  val fileIO: FileIOInterface,
  private val undoManager: UndoManager,
  private val cardProvider: CardProvider,
) extends ControllerInterface {

  var field: FieldInterface = Field(
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

  var errorMsg: Option[String] = None

  def canUndo: Boolean = undoManager.canUndo()
  def canRedo: Boolean = undoManager.canRedo()

  def placeCard(move: Move): Unit = doStep(new PlaceCardCommand(field, move))
  def drawCard(): Unit = doStep(new DrawCardCommand(field))
  def setPlayerNames(playername1: String, playername2: String): Unit = {
    field = field.setPlayerNames(playername1, playername2)
    nextState()
    notifyObservers(Event.PLAY, msg = None)
  }
  def attack(move: Move): Unit = doStep(new AttackCommand(field, move))
  def directAttack(move: Move): Unit = doStep(
    new DirectAttackCommand(field, move)
  )
  def switchPlayer(): Unit = doStep(new SwitchPlayerCommand(field))

  def doStep(command: CommandInterface): Unit = {
    command.doStep match {
      case Success(newField) => // noinspection RedundantBlock
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

  def exitGame(): Unit = {
    errorMsg = None
    setGameState(GameState.EXIT)
  }

  def nextState(): Unit = {
    field = field.setGameState(field.gameState match {
      case GameState.CHOOSEMODE       => GameState.ENTERPLAYERNAMES
      case GameState.ENTERPLAYERNAMES => GameState.MAINGAME
      case GameState.MAINGAME         => GameState.WIN
    })
  }

  def setStrategy(strat: Strategy): Unit = {
    field = strat match {
      case Strategy.normal   => field.setHpValues(30).setManaValues(1)
      case Strategy.hardcore => field.setHpValues(10).setManaValues(10)
      case Strategy.debug    => field.setHpValues(100).setManaValues(100)
    }
    nextState()
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
}
