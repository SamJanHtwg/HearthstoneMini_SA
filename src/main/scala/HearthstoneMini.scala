package hearthstoneMini

import core.controller.module.ControllerModule
import gui.GuiApp
import tui.TuiService

object HearthstoneMini {
  def main(args: Array[String]): Unit = {

    TuiService
    // TuiRestService
    // GuiService
    
    // ControllerService
    new GuiApp(ControllerModule.given_ControllerInterface)
  }
}
