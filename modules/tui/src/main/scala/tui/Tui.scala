package tui

import model.Move
import core.util.Observer
import core.util.Event
import model.GameState
import core.controller.Strategy
import core.controller.component.controllerImpl.Controller
import scala.util.{Failure, Success, Try}
import core.controller.component.ControllerInterface
import core.util.matrix.Matrix
import model.playerComponent.PlayerInterface
import Tui.{
  zeilenUmbruch,
  endGameMsg,
  chooseGameMode,
  enterPlayerNames,
  istDranMsg,
  gewonnenMsg,
  commands,
  // cleanScreen,
  colorYellow,
  boldText,
  resetStyles
}

/*
Ansi Codes: https://gist.github.com/dominikwilkowski/60eed2ea722183769d586c76f22098dd
 */

object Tui {
  val zeilenUmbruch: String = "\n"
  val endGameMsg: String = "Schönes Spiel!"
  val chooseGameMode: String = "Bitte Spielmodus auswählen: " +
    "\n[1] Normal: Start mit: 30 Healthpoints & 1 Mana" +
    "\n[2] Hardcore: Start mit: 10 Healthpoints & 5 Mana " +
    "\n[3] Admin: Start mit: 100 Healthpints & 100 Mana"
  val enterPlayerNames: String = "Bitte Spielernamen 1 & 2 eingeben: "
  val istDranMsg: String = " ist dran!"
  val gewonnenMsg: String = " hat gewonnen!!"
  val commands: String =
    "place(hand,solt) | d-draw() | a-attack(yours, theirs) |" +
      " e-direct attack | " + "s-Endturn |" + "\n" + "z-undo | y-redo | q-Quit"
  // val cleanScreen: String = "\u001b[2J"
  val colorYellow: String = "\u001b[33m"
  val boldText: String = "\u001b[1m"
  val resetStyles: String = "\u001b[0m"
}

class Tui(controller: ControllerInterface) extends Observer {
  val offset: Int = 1
  val slotNum: Int = 5
  val cardWidth: Int = 15
  val cardHeight: Int = 5
  val slotWidth: Int = cardWidth + 2 // 2 for Margin
  val fieldBarHeight: Int = cardHeight
  val gameBarHeight: Int = 5
  val menuBarHeight: Int = 3
  val fieldWidth: Int = slotNum * slotWidth

  val playerHeight: Int =
    fieldBarHeight + (2 * offset) + gameBarHeight + menuBarHeight
  val standartFieldHeight: Int =
    playerHeight * 2
      + offset
  controller.add(this)

  override def update(event: Event, msg: Option[String] = None): Unit = {
    event match {
      case Event.ERROR => msg.fold({})(msg => println(msg))
      case Event.EXIT  => println(endGameMsg)
      case Event.PLAY =>
        controller.field.gameState match {
          case GameState.CHOOSEMODE       => println(chooseGameMode)
          case GameState.ENTERPLAYERNAMES => println(enterPlayerNames)
          case GameState.MAINGAME         => printField()
          case GameState.WIN =>
            println(
              zeilenUmbruch + controller.getWinner().getOrElse(" ")
                + gewonnenMsg
            )
        }
    }
  }

  def onInput(input: String): Unit = {
    if checkInput(input) then {
      controller.field.gameState match {
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
    // print(cleanScreen)
    print(resetStyles)
    println(buildFieldMatrix.toString)
    print(colorYellow + boldText)
    println(commands)
  }

  private def buildFieldMatrix: Matrix = {
    var matrix = new Matrix(
      standartFieldHeight,
      slotWidth * 5,
      " "
    )

    controller.field.players.map(player => {
      val playerMatrix = player._2.field.toString()
      val index =
        if player._2.id % 2 == 1 then offset
        else playerHeight + offset
      matrix = matrix
        .updateMatrix(
          index,
          0,
          if player._2.id % 2 == 1 then renderUnevenId(player._2)
          else renderEvenId(player._2)
        )
        .updateMatrix(playerHeight, 0, List[String]("-" * fieldWidth))
    })
    matrix
  }

  def checkInput(input: String): Boolean = {
    controller.field.gameState match {
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
    .name + istDranMsg +
    zeilenUmbruch + controller.field.toString + commands

  private def menuBar(player: PlayerInterface): Matrix = new Matrix(
    menuBarHeight,
    fieldWidth,
    " "
  )
    .updateMatrix(
      0,
      0,
      List[String](
        player.name + " " +
          "#" * ((fieldWidth - player.name.length - 1) *
            player.hpValue / player.maxHpValue)
            .asInstanceOf[Float]
            .floor
            .asInstanceOf[Int],
        "Mana: " + player.manaValue + "/" + player.maxManaValue + " " +
          "Healh: " + player.hpValue + "/" + player.maxHpValue + " " +
          "Deck: " + player.deck.length + " " +
          "Friedhof: " + player.friedhof.length,
        "-" *
          fieldWidth
      )
    )

  private def renderUnevenId(player: PlayerInterface): Matrix = {
    var initialMatrix = new Matrix(
      playerHeight,
      fieldWidth,
      " "
    )
      .updateMatrix(0, 0, menuBar(player))

    initialMatrix = player.field.zipWithIndex.foldLeft(initialMatrix) {
      case (matrix, (card, index)) =>
        card
          .map(card =>
            matrix.updateMatrix(
              gameBarHeight + menuBarHeight + offset,
              offset + index * slotWidth,
              card.toString.split("#")
            )
          )
          .fold(matrix)(matrix.updateMatrix(0, 0, _))
    }
    initialMatrix = player.hand.zipWithIndex.foldLeft(initialMatrix) {
      case (matrix, (card, index)) =>
        matrix.updateMatrix(
          menuBarHeight,
          offset + index * slotWidth,
          card.toString.split("#")
        )
    }
    initialMatrix
      .updateMatrix(
        menuBarHeight + gameBarHeight,
        0,
        List[String]("-" * fieldWidth)
      )

  }

  private def renderEvenId(player: PlayerInterface): Matrix = {
    var initialMatrix = new Matrix(
      playerHeight,
      fieldWidth,
      " "
    )
      .updateMatrix(
        gameBarHeight + fieldBarHeight + (offset * 2),
        0,
        menuBar(player)
      )

    val fieldMatrixStartRow = gameBarHeight + menuBarHeight

    initialMatrix = player.field.zipWithIndex.foldLeft(initialMatrix) {
      case (matrix, (card, index)) =>
        card
          .map(card =>
            matrix.updateMatrix(
              offset,
              index * slotWidth,
              card.toString.split("#")
            )
          )
          .fold(matrix)(matrix.updateMatrix(0, 0, _))
    }
    initialMatrix = player.hand.zipWithIndex.foldLeft(initialMatrix) {
      case (matrix, (card, index)) =>
        matrix.updateMatrix(
          fieldBarHeight + offset,
          offset + index * slotWidth,
          card.toString.split("#")
        )
    }
    initialMatrix
      .updateMatrix(
        fieldBarHeight,
        0,
        List[String]("-" * fieldWidth)
      )
      .updateMatrix(
        fieldBarHeight + gameBarHeight + offset,
        0,
        List[String]("-" * fieldWidth)
      )
  }
}
