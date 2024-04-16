package hearthstoneMini

import scala.io.StdIn
import scala.io.StdIn.readLine
import core.controller.component.ControllerInterface
import model.fieldComponent.fieldImpl.Field
import core.controller.component.controllerImpl.Controller
import tui.Tui
import gui.GuiApp
import core.util.Event
import core.controller.GameState


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
  val controller: ControllerInterface = Controller(new Field())
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
