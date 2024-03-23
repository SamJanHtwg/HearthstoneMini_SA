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

  override def update(e: Event, msg: Option[String]) = {
    e match {
      case Event.ERROR => msg.fold({})((msg) => println(msg))
      case Event.EXIT => println(Strings.endGameMsg)
      case Event.PLAY => {
        controller.gameState match {
          case GameState.CHOOSEMODE => println(Strings.chooseGameMode)
          case GameState.ENTERPLAYERNAMES => println(Strings.enterPlayerNames)
          case GameState.MAINGAME => printField()
          case GameState.WIN => println(Strings.zeilenUmbruch + controller.getWinner().getOrElse(" ")
            + Strings.gewonnenMsg)
        }
      }
    }
  }


  def onInput(input: String) = {
    if checkInput(input) then {
      controller.gameState match {
        case GameState.CHOOSEMODE => setGameStrategy(input)
        case GameState.ENTERPLAYERNAMES => setPlayerNames(input)
        case GameState.MAINGAME => EvalInput(input)
      }
    }
  }

  def setGameStrategy(input: String): Unit = {
    controller.setStrategy(input.toCharArray.head.asDigit match {
      case 1 => Strategy.normal
      case 2 => Strategy.hardcore
      case 3 => Strategy.debug
    })
  }

  def setPlayerNames(input: String): Unit = {
    val splitInput = input.split(" ")
    controller.setPlayerNames(Move(p1 = splitInput(0), p2 = splitInput(1)))
  }

  def printField(): Unit = {
    print(Strings.cleanScreen)
    println(controller.field.players(0).name + Strings.istDranMsg)
    println()
    println(controller.field.toString + Strings.zeilenUmbruch)
    println(Strings.commands)

  }
  def checkInput(input: String): Boolean = {
    controller.gameState match {
      case GameState.CHOOSEMODE => input.matches("([123])")
      case GameState.ENTERPLAYERNAMES => input.matches("(.{3,10}\\s.{3,10})")
      case GameState.MAINGAME => input.matches("([pa]\\d\\d)|([qdszy])|([e]\\d)")
    }
  }

  def EvalInput(input: String): Unit = {
    val chars = input.toCharArray
    chars(0) match
      case 'q' => controller.exitGame()
      case 'p' => controller.placeCard(Move(chars(1).asDigit - 1, chars(2).asDigit - 1))
      case 'd' => controller.drawCard()
      case 'a' => controller.attack(Move(fieldSlotActive = chars(1).asDigit - 1,
        fieldSlotInactive = chars(2).asDigit - 1))
      case 'e' => controller.directAttack(Move(fieldSlotActive = chars(1).asDigit - 1))
      case 's' => controller.switchPlayer()
      case 'z' => controller.undo
      case 'y' => controller.redo
  }

  override def toString(): String = controller.field.players(0).name + Strings.istDranMsg +
    Strings.zeilenUmbruch + controller.field.toString + Strings.commands
}

