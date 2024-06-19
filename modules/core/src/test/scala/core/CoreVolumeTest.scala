// package core

// import scala.concurrent.duration._
// import io.gatling.core.Predef._
// import io.gatling.http.Predef._
// import io.gatling.jdbc.Predef._
// import play.api.libs.json.Json
// import model.GameState
// import model.Move
// import core.controller.Strategy

// class CoreVolumeTest extends CoreSimulationSkeleton {
//   override def executeOperations(): Unit = {
//     var scn = buildScenario("Volume Test Scenario - Managable")
//     var scn2 = buildScenario("Volume Test Scenario - Overload")

//     setUp(
//      scn.inject(
//         constantUsersPerSec(100) during (30.seconds)
//       ).andThen(
//         scn2.inject(
//           constantUsersPerSec(500) during (30.seconds)
//         )
//       )
//     ).protocols(httpProtocol)
//   }

//   executeOperations()
// }
