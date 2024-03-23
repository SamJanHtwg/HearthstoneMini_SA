package hearthstoneMini

import aview.Gui.GUIApp
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

object HearthstoneMini {
  val hearthstoneMiniRunner = new HearthstoneMiniRunner()

  def main(args: Array[String]): Unit = {
    hearthstoneMiniRunner.play()
  }
}

class HearthstoneMiniRunner(initGUI: Boolean = false, initTUI: Boolean = false) {
  val controller: ControllerInterface = Controller(new Field(5))
  val optionalTui: Option[Tui] = if (initTUI) Some(new Tui(controller)) else None
  val optionalGUI: Option[GUIApp] = if (initGUI) Some(new GUIApp(controller)) else None
  
  def play(): Unit = {
    optionalTui.fold(null)( 
      tui => 
        tui.update(Event.PLAY, None)
        while controller.gameState != GameState.EXIT && controller.gameState != GameState.WIN do {
          tui.onInput(StdIn.readLine())
        }
    )
  }
}





