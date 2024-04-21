package tui

import scala.io.StdIn.readLine
import tui.Tui
import java.io.IOException
import core.controller.module.ControllerModule
import core.util.Event
import scala.io.StdIn
import core.controller.component.ControllerInterface

import core.controller.module.ControllerRestClientModule
import model.GameState

object TuiService extends App:
  Starter(ControllerModule.given_ControllerInterface).start()

object TuiRestService extends App:
  Starter(ControllerRestClientModule.given_ControllerInterface).start()

case class Starter(controller: ControllerInterface) {
  val tui = Tui(controller)

  def start() = {
    tui.update(Event.PLAY, None)
    while controller.field.gameState != GameState.EXIT && controller.field.gameState != GameState.WIN
    do {
      tui.onInput(StdIn.readLine())
    }
  }
}
