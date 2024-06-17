package hearthstoneMini

import core.controller.module.ControllerModule
// import Gui.GuiModule
// import tui.TuiService
import core.controller.module.ControllerModule
import tui.TuiRestService
import Gui.GuiRestModule
import persistence.fileIO.service.PersistenceService
import persistence.PersistenceRestApi

object HearthstoneMini {
  def main(args: Array[String]): Unit = {
    PersistenceRestApi.main(args)
    ControllerModule
    GuiRestModule
    // TuiRestService
    // TuiService
    // GuiService
    // ControllerService
  }
}
