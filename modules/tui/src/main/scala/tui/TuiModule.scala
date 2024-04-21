package tui

import scala.io.StdIn.readLine
import tui.Tui
import java.io.IOException
import core.controller.module.ControllerModule
import core.util.Event
import scala.io.StdIn
import core.controller.component.ControllerInterface
import core.controller.GameState
import core.controller.module.ControllerRestModule

object TuiService extends App:
  Starter(ControllerModule.given_ControllerInterface).start()

object TuiRestService extends App:
  Starter(ControllerRestModule.given_ControllerInterface).start()

case class Starter(controller: ControllerInterface) {
  val tui = Tui(controller)

  def start() = {
    tui.update(Event.PLAY, None)
    while controller.gameState != GameState.EXIT && controller.gameState != GameState.WIN
    do {
      tui.onInput(StdIn.readLine())
    }
  }
}

//   def processInput(): Unit = {
//     try {
//       val input = readLine()
//       if (input != "quit") {
//         tui.processInputLine( input )
//         processInput()
//       }
//     } catch {
//       case e: IOException => e.printStackTrace()
//     }
