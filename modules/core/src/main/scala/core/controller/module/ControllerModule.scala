package core.controller.module

import core.controller.component.ControllerInterface
import core.controller.component.controllerImpl.Controller
import persistence.fileIO.jsonIOImpl.JsonIO
import core.controller.component.controllerImpl.ControllerRestClient

object ControllerModule:
  given ControllerInterface = Controller(new JsonIO())

object ControllerRestModule:
  given ControllerInterface = ControllerRestClient(new JsonIO())
