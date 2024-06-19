// package persistence

// import scala.concurrent.duration._
// import io.gatling.core.Predef._
// import io.gatling.http.Predef._
// import io.gatling.jdbc.Predef._
// import play.api.libs.json.Json

// class PersistenceVolumeTest extends PersistenceSimulationSkeleton {
//   override def executeOperations(): Unit = {
//     var scn = buildScenario("Volume Test Scenario - Managable")
//     var scn2 = buildScenario("Volume Test Scenario - Overload")

//     setUp(
//      scn.inject(
//         constantUsersPerSec(100) during (10.seconds)
//       ).andThen(
//         scn2.inject(
//           constantUsersPerSec(500) during (10.seconds)
//         )
//       )
//     ).protocols(httpProtocol)
//   }

//   executeOperations()
// }
