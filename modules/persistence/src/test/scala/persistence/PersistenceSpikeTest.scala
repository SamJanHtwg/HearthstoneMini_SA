// package persistence

// import scala.concurrent.duration._
// import io.gatling.core.Predef._
// import io.gatling.http.Predef._
// import io.gatling.jdbc.Predef._
// import play.api.libs.json.Json

// class PersistenceSpikeTest extends PersistenceSimulationSkeleton {
//   override def executeOperations(): Unit = {
//     var scn = buildScenario("Spike Test Scenario - Managable")
//     var scn2 = buildScenario("Spike Test Scenario - Overload")

//     setUp(
//       scn
//         .inject(
//           atOnceUsers(100),
//           nothingFor(5.seconds),
//           atOnceUsers(100),
//         )
//         .andThen(
//           scn2.inject(
//             atOnceUsers(500),
//             nothingFor(5.seconds),
//             atOnceUsers(500),
//           )
//         )
//     ).protocols(httpProtocol)
//   }

//   executeOperations()
// }
