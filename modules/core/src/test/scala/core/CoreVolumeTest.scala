package core

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import play.api.libs.json.Json
import model.GameState
import model.Move
import core.controller.Strategy

class CoreVolumeTest extends CoreSimulationSkeleton {
  override def executeOperations(): Unit = {
    var scn = buildScenario("Spike Test Scenario - Managable")
    var scn2 = buildScenario("Spike Test Scenario - Overload")
    var scn3 = buildScenario("Spike Test Scenario - Ramp Down")

    setUp(
     scn.inject(
        constantUsersPerSec(100) during (20.seconds)
      ).andThen(
        scn2.inject(
          constantUsersPerSec(1000) during (60.seconds)
        )
      ).andThen(
        scn3.inject(
          constantUsersPerSec(10000) during (2.minutes)
        )
      )
    ).protocols(httpProtocol)
  }

  executeOperations()
}
