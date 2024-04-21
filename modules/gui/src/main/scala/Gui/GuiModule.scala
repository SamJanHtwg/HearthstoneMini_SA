package Gui

import Gui.Gui
import core.controller.module.ControllerModule
import core.controller.module.ControllerRestClientModule

object GuiModule extends App:
  Gui(using ControllerModule.given_ControllerInterface).main(Array.empty)
object GuiRestModule extends App:  
  Gui(using ControllerRestClientModule.given_ControllerInterface).main(Array.empty)

