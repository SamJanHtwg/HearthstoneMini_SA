package hearthstoneMini

import core.controller.module.ControllerModule
import Gui.GuiModule
import tui.TuiService
import core.controller.module.ControllerServiceModule
import tui.TuiRestService
import Gui.GuiRestModule


object HearthstoneMini {
  def main(args: Array[String]): Unit = {
    
    ControllerServiceModule
    GuiRestModule
    // TuiService
    TuiRestService
    // GuiService
    // ControllerService

  }
}
