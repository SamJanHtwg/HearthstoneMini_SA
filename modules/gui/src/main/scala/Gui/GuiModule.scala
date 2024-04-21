package Gui

import Gui.Gui
import core.controller.module.ControllerModule
import core.controller.module.ControllerRestClientModule
import core.controller.component.ControllerInterface
import model.GameState
import core.util.Event
import scala.io.StdIn

object GuiModule {
  Starter(ControllerModule.given_ControllerInterface).start()
}

object GuiRestModule {
  Starter(ControllerRestClientModule.given_ControllerInterface).start()
}

 class Starter(controller: ControllerInterface) {
  val thread: Thread = new Thread {
    override def run(): Unit = {
      val gui = Gui(using controller)
  
      gui.main(Array.empty)
    }
  }

  def start(): Unit = {
    thread.start()
  }
}
