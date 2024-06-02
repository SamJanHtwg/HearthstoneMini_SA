package core

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import play.api.libs.json.Json
import model.GameState
import model.Move

class CoreLoadTest extends SimulationSkeleton {
  override val operations = List(
    buildOperation("API root", "GET", "/", StringBody("")),
    buildOperation("Place a card", "POST", "/controller/placeCard", StringBody(Json.stringify(Move(handSlot = 0, fieldSlotActive = 0).toJson))),
    buildOperation("Undo step", "GET", "/controller/undo", StringBody("")),
    buildOperation("Redo step", "GET", "/controller/redo", StringBody("")),
  )

  override def executeOperations(): Unit = {
    var scn = buildScenario("Load Test Scenario - Managable")
    var scn2 = buildScenario("Load Test Scenario - Overload")

    setUp(
      scn.inject(
        rampUsers(2000) during (20.seconds)
      ),
      scn2.inject(
        rampUsers(10000) during (20.seconds)
      ),
    ).protocols(httpProtocol)
  }

  executeOperations()
}
