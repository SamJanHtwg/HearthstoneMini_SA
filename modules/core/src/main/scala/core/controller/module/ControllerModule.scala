package core.controller.module

import core.controller.component.ControllerInterface
import core.controller.component.controllerImpl.Controller
import persistence.fileIO.jsonIOImpl.JsonIO
import core.controller.component.controllerImpl.ControllerRestClient
import core.controller.service.RestBackendService
import core.util.*
import core.controller.service.HttpService
import core.controller.component.BackendServiceInterface
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure

object ControllerModule:
  private val executorService = Executors.newSingleThreadExecutor()
  private implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
  private var backendService: BackendServiceInterface = RestBackendService(using
      given_HttpService
    )

  private val backendServiceFuture: Future[Unit] = Future {
    backendService.start()
  }

  
  backendServiceFuture.onComplete {
    case Success(service) =>
      println("Backend service started successfully.")
    case Failure(e) =>
      println(s"Failed to start the backend service: ${e.getMessage}")
      executorService.shutdown()
  }

  private val controller = Controller(
    new JsonIO(),
    new UndoManager(),
    new CardProvider(inputFile = "/json/cards.json"),
    backendService
  )

  given HttpService = HttpService()

object ControllerRestClientModule:
  given ControllerInterface = ControllerRestClient(using
    ControllerModule.given_HttpService
  )