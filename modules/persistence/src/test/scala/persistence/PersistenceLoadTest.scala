package persistence

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import play.api.libs.json.Json

class PersistenceLoadTest extends PersistenceSimulationSkeleton {
  override def executeOperations(): Unit = {
    var scn = buildScenario("Load Test Scenario - Managable")
    var scn2 = buildScenario("Load Test Scenario - Overload")

    setUp(
      scn
        .inject(
          rampUsers(2000) during (20.seconds)
        )
        .andThen(
          scn2.inject(
            rampUsers(10000) during (20.seconds)
          )
        )
    ).protocols(httpProtocol)
  }

  executeOperations()
}