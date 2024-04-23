package core.controller.module

import core.controller.component.ControllerInterface
import core.controller.component.controllerImpl.Controller
import persistence.fileIO.jsonIOImpl.JsonIO
import core.controller.component.controllerImpl.ControllerRestClient
import core.controller.service.ControllerRestService
import core.util.*

object ControllerModule:
  given ControllerInterface = Controller(
    new JsonIO(),
    new UndoManager(),
    new CardProvider(inputFile = "/json/cards.json")
  )

object ControllerRestClientModule:
  given ControllerInterface = ControllerRestClient()

object ControllerServiceModule extends App:
  ControllerRestService(using ControllerModule.given_ControllerInterface)
    .start()
