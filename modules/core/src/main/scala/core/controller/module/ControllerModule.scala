package core.controller.module

import core.controller.component.ControllerInterface
import core.controller.component.controllerImpl.Controller
import persistence.fileIO.jsonIOImpl.JsonIO
import core.controller.component.controllerImpl.ControllerRestClient
import core.controller.service.ControllerService
import core.util.*
import core.controller.service.HttpService

object ControllerModule:
  given ControllerInterface = Controller(
    new JsonIO(),
    new UndoManager(),
    new CardProvider(inputFile = "/json/cards.json")
  )
  given HttpService = HttpService()

object ControllerRestClientModule:
  given ControllerInterface = ControllerRestClient(using
    ControllerModule.given_HttpService
  )

object ControllerServiceModule extends App:
  ControllerService(using
    ControllerModule.given_ControllerInterface,
    ControllerModule.given_HttpService
  )
    .start()
