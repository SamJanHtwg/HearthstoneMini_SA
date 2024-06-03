package core

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import play.api.libs.json.Json
import model.GameState
import model.Move
import core.controller.Strategy

class CoreSpikeTest extends CoreSimulationSkeleton {
  override def executeOperations(): Unit = {
    var scn = buildScenario("Spike Test Scenario - Managable")
    var scn2 = buildScenario("Spike Test Scenario - Overload")

    setUp(
      scn
        .inject(
          rampUsers(20) during (20.seconds),
          atOnceUsers(1000),
          rampUsers(1000) during (20.seconds)
        )
        .andThen(
          scn2.inject(
            rampUsers(20) during (10.seconds),
            atOnceUsers(10000)
          )
        )
    ).protocols(httpProtocol)
  }

  executeOperations()
}
