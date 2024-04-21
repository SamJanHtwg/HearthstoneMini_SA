package core
package controller.component
import model.Move
import model.fieldComponent.FieldInterface
import util.Observable
import model.GameState.GameState
import core.controller.Strategy.*

//noinspection AccessorLikeMethodIsEmptyParen,UnitMethodIsParameterless
trait ControllerInterface extends Observable {
  var errorMsg: Option[String]
  var field: FieldInterface
  def setGameState(gameState: GameState): Unit
  def canUndo: Boolean
  def canRedo: Boolean
  def placeCard(move: Move): Unit
  def drawCard(): Unit
  def setPlayerNames(playername1: String, playername2: String): Unit
  def attack(move: Move): Unit
  def directAttack(move: Move): Unit
  def switchPlayer(): Unit
  def exitGame(): Unit
  def undo: Unit
  def redo: Unit
  def setStrategy(strat: Strategy): Unit
  override def toString(): String
  def getWinner(): Option[String]
  def loadField: Unit
  def saveField: Unit
}
