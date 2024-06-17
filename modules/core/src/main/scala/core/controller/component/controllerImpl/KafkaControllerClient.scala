package core.controller.component.controllerImpl

import core.controller.component.ControllerInterface
import model.fieldComponent.FieldInterface
import model.GameState.GameState
import model.Move
import core.controller.Strategy.Strategy

class KafkaControllerClient extends ControllerInterface{
  override def exitGame(): Unit = ???

  override def placeCard(move: Move): Unit = ???

  override def setPlayerNames(playername1: String, playername2: String): Unit = ???

  override def switchPlayer(): Unit = ???

  override def attack(move: Move): Unit = ???

  override def canRedo: Boolean = ???

  override def canUndo: Boolean = ???

  override def setStrategy(strat: Strategy): Unit = ???

  override def redo: Unit = ???

  override def undo: Unit = ???

  override def loadField: Unit = ???

  override def directAttack(move: Move): Unit = ???

  override def saveField: Unit = ???

  override def setGameState(gameState: GameState): Unit = ???

  override def deleteField: Unit = ???

  override def getWinner(): Option[String] = ???

  override def drawCard(): Unit = ???
  
}