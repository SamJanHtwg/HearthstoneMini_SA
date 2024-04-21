package core.controller.module

import core.controller.component.ControllerInterface
import core.controller.component.controllerImpl.Controller
import model.fileIOComponent.jsonIOImpl.FileIO
import core.controller.component.controllerImpl.ControllerRestClient

object ControllerModule:
  given ControllerInterface = Controller(new FileIO())

object ControllerRestModule:
  given ControllerInterface = ControllerRestClient(new FileIO())
