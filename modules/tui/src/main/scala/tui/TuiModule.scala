package tui

import scala.io.StdIn.readLine
import tui.Tui
import java.io.IOException
import core.controller.module.ControllerModule
import core.util.Event
import scala.io.StdIn
import core.controller.component.ControllerInterface

import core.controller.module.ControllerRestClientModule
import core.model.GameState

object TuiService:
  Starter(ControllerModule.given_ControllerInterface).start()

object TuiRestService:
  Starter(ControllerRestClientModule.given_ControllerInterface).start()

case class Starter(controller: ControllerInterface) {
  val thread: Thread = new Thread {
    override def run(): Unit = {
      val tui = Tui(controller)

      tui.update(Event.PLAY, None)
      while controller.field.gameState != GameState.EXIT && controller.field.gameState != GameState.WIN
      do {
        tui.onInput(StdIn.readLine())
      }
    }
  }

  def start() = {
    thread.start()
  }
}
