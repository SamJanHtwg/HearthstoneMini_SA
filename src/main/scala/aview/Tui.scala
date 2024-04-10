package hearthstoneMini
package aview

import model.Move
import util.Observer
import util.Event
import controller.GameState
import controller.Strategy
import controller.component.controllerImpl.Controller
import scala.util.{Failure, Success, Try}
import hearthstoneMini.controller.component.ControllerInterface

class Tui(controller: ControllerInterface) extends Observer {
  controller.add(this)

  override def update(e: Event, msg: Option[String]): Unit = {
    e match {
      case Event.ERROR => msg.fold({})(msg => println(msg))
      case Event.EXIT  => println(Strings.endGameMsg)
      case Event.PLAY =>
        controller.gameState match {
          case GameState.CHOOSEMODE       => println(Strings.chooseGameMode)
          case GameState.ENTERPLAYERNAMES => println(Strings.enterPlayerNames)
          case GameState.MAINGAME         => printField()
          case GameState.WIN =>
            println(
              Strings.zeilenUmbruch + controller.getWinner().getOrElse(" ")
                + Strings.gewonnenMsg
            )
        }
    }
  }

  def onInput(input: String): Unit = {
    if checkInput(input) then {
      controller.gameState match {
        case GameState.CHOOSEMODE       => setGameStrategy(input)
        case GameState.ENTERPLAYERNAMES => setPlayerNames(input)
        case GameState.MAINGAME         => EvalInput(input)
      }
    }
  }

  private def setGameStrategy(input: String): Unit = {
    controller.setStrategy(input.toCharArray.head.asDigit match {
      case 1 => Strategy.normal
      case 2 => Strategy.hardcore
      case 3 => Strategy.debug
    })
  }

  def setPlayerNames(input: String): Unit = {
    val splitInput = input.split(" ")
    controller.setPlayerNames(
      playername1 = splitInput(0),
      playername2 = splitInput(1)
    )
  }

  private def printField(): Unit = {
    print(Strings.cleanScreen)
    println(
      controller.field
        .players(controller.field.activePlayerId)
        .name + Strings.istDranMsg
    )
    println()
    println(controller.field.toString + Strings.zeilenUmbruch)
    println(Strings.commands)

  }
  def checkInput(input: String): Boolean = {
    controller.gameState match {
      case GameState.CHOOSEMODE       => input.matches("([123])")
      case GameState.ENTERPLAYERNAMES => input.matches("(.{3,10}\\s.{3,10})")
      case GameState.MAINGAME =>
        input.matches("([pa]\\d\\d)|([qdszy])|([e]\\d)")
    }
  }

  private def EvalInput(input: String): Unit = {
    val chars = input.toCharArray
    chars(0) match
      case 'q' => controller.exitGame()
      case 'p' =>
        controller.placeCard(Move(chars(1).asDigit - 1, chars(2).asDigit - 1))
      case 'd' => controller.drawCard()
      case 'a' =>
        controller.attack(
          Move(
            fieldSlotActive = chars(1).asDigit - 1,
            fieldSlotInactive = chars(2).asDigit - 1
          )
        )
      case 'e' =>
        controller.directAttack(Move(fieldSlotActive = chars(1).asDigit - 1))
      case 's' => controller.switchPlayer()
      case 'z' => controller.undo
      case 'y' => controller.redo
  }

  override def toString(): String = controller.field
    .players(controller.field.activePlayerId)
    .name + Strings.istDranMsg +
    Strings.zeilenUmbruch + controller.field.toString + Strings.commands
}

/* 
  // CARD
  override def toMatrix: Matrix[String] = new Matrix[String](
    FieldObject.standartCardHeight,
    FieldObject.standartCardWidth,
    " "
  ).updateMatrix(0, 0, toString().split("#").toList)

  // FIELD
  override def toMatrix: Matrix[String] = matrix
    .updateMatrix(0, 0, List[String]("-" * FieldObject.standartFieldWidth))
    .updateMatrixWithMatrix(FieldObject.offset, 0, getPlayerById(1).toMatrix)
    .updateMatrixWithMatrix(
      FieldObject.offset + FieldObject.standartMenueBarHeight + FieldObject.standartGameBarHeight
        + FieldObject.standartFieldBarHeight,
      0,
      getPlayerById(2).toMatrix
    )

  val offset: Int = 1
  
  val standartCardWidth: Int = 15
  val standartCardHeight: Int = 5
  val standartSlotWidth: Int = standartCardWidth + 2 // 2 for Margin
  val standartFieldBarHeight: Int = standartCardHeight + 1 // + 2 for border
  val standartGameBarHeight: Int = 7
  val standartMenueBarHeight: Int = 2
  val standartFieldWidth: Int = standartSlotNum * standartSlotWidth
  val standartFieldHeight: Int =
    (standartFieldBarHeight + standartGameBarHeight + standartMenueBarHeight) * 2
      + FieldObject.offset
 */