package hearthstoneMini

import model.*
import controller.GameState
import aview.Tui
import controller.component.controllerImpl.Controller
import model.fieldComponent.fieldImpl.Field
import scala.io.StdIn
import scala.io.StdIn.readLine
import util.Event
import scalafx.scene.text.FontWeight.Bold
import hearthstoneMini.controller.component.ControllerInterface
import hearthstoneMini.aview.gui.GuiApp

object HearthstoneMini {
  private val hearthstoneMiniRunner =
    new HearthstoneMiniRunner(initGUI = true, initTUI = true)

  def main(args: Array[String]): Unit = {
    hearthstoneMiniRunner.play()
  }
}

class HearthstoneMiniRunner(
    initGUI: Boolean = false,
    initTUI: Boolean = false
) {
  val controller: ControllerInterface = Controller(new Field(5))
  private val optionalTui: Option[Tui] =
    if (initTUI) Some(new Tui(controller)) else None
  val optionalGUI: Option[GuiApp] =
    if (initGUI) Some(new GuiApp(controller)) else None

  def play(): Unit = {
    optionalTui match {
      case None => {}
      case Some(tui) =>
        tui.update(Event.PLAY, None)
        while controller.gameState != GameState.EXIT && controller.gameState != GameState.WIN
        do {
          tui.onInput(StdIn.readLine())
        }
    }
  }
}
