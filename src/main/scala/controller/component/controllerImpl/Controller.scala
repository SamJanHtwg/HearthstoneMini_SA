package hearthstoneMini
package controller.component.controllerImpl

import com.google.inject.name.{Named, Names}
import com.google.inject.{Guice, Inject, Injector}
import controller.GameState.*
import controller.component.ControllerInterface
import controller.{GameState, Strategy}
import model.fieldComponent.FieldInterface
import model.Move
import model.fileIOComponent.FileIOInterface
import model.playerComponent.playerImpl.Player
import net.codingwell.scalaguice.InjectorExtensions.*
import util.{Event, Observable, UndoManager}
import util.commands.CommandInterface

import java.lang.System.exit
import java.text.Annotation
import scala.util.{Failure, Success, Try}
import util.commands.commandImpl.{
  PlaceCardCommand,
  DirectAttackCommand,
  DrawCardCommand,
  SwitchPlayerCommand,
  AttackCommand
}

case class Controller @Inject() (var field: FieldInterface)
    extends ControllerInterface {
  private val injector: Injector =
    Guice.createInjector(new HearthstoneMiniModule)
  private val fileIO: FileIOInterface =
    injector.getInstance(classOf[FileIOInterface])
  var gameState: GameState = GameState.CHOOSEMODE
  var errorMsg: Option[String] = None
  private val undoManager: UndoManager = new UndoManager

  def canUndo: Boolean = undoManager.canUndo
  def canRedo: Boolean = undoManager.canRedo

  def placeCard(move: Move): Unit = doStep(new PlaceCardCommand(this, move))
  def drawCard(): Unit = doStep(new DrawCardCommand(this))
  def setPlayerNames(playername1: String, playername2: String): Unit = {
    field = field.setPlayerNames(playername1, playername2)
    nextState()
    notifyObservers(Event.PLAY, msg = None)
  }
  def attack(move: Move): Unit = doStep(new AttackCommand(this, move))
  def directAttack(move: Move): Unit = doStep(
    new DirectAttackCommand(this, move)
  )
  def switchPlayer(): Unit = doStep(new SwitchPlayerCommand(this))

  private def doStep(command: CommandInterface): Unit = {
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
    undoManager.undoStep
    errorMsg = None
    notifyObservers(Event.PLAY, msg = None)
  }
  def redo: Unit = {
    undoManager.redoStep
    errorMsg = None
    notifyObservers(Event.PLAY, msg = None)
  }
  def exitGame(): Unit = {
    gameState = GameState.EXIT
    errorMsg = None
    notifyObservers(Event.EXIT, msg = None)
  }
  def nextState(): Unit = {
    gameState match {
      case GameState.CHOOSEMODE       => gameState = GameState.ENTERPLAYERNAMES
      case GameState.ENTERPLAYERNAMES => gameState = GameState.MAINGAME
      case GameState.MAINGAME         => gameState = GameState.WIN
    }
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
        gameState = GameState.WIN
        Some(playersWithHp.values.head.name)
      case _ => None
    }
  }
  def saveField: Unit = {
    fileIO.save(this.field)
  }
  def loadField: Unit = {
    fileIO.load match {
      case Success(value) =>
        this.field = value
        gameState = GameState.MAINGAME
        errorMsg = None
        notifyObservers(Event.PLAY, msg = None)
      case Failure(exception) =>
        errorMsg = Some("Sieht so aus als wäre die Datei beschädigt.")
        notifyObservers(Event.ERROR, msg = errorMsg)
    }
  }
}
