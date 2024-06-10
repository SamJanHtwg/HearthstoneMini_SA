package persistence

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import play.api.libs.json.Json

class PersistenceStressTest extends PersistenceSimulationSkeleton {
  override def executeOperations(): Unit = {
    var scn = buildScenario("Stress Test Scenario - Managable")
    var scn2 = buildScenario("Stress Test Scenario - Overload")

    setUp(
      scn
        .inject(
            rampUsers(10) during (10.seconds),
            stressPeakUsers(100) during (30.seconds),
        )
        .andThen(
          scn2.inject(
            rampUsers(10) during (10.seconds),
            stressPeakUsers(500) during (30.seconds)
          )
        )
    ).protocols(httpProtocol)
  }

  executeOperations()
}
