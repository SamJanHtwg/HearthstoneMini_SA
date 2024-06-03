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
  override val operations = List(
    buildOperation("Reset", "POST", "/controller/setGameState", StringBody(Json.stringify(Json.obj("gameState" -> GameState.MAINGAME.toString())))),
    buildOperation("Reset the Game", "GET", "/controller/delete", StringBody("")),
    buildOperation("Start a new Game", "POST", "/controller/setStrategy", StringBody(Json.stringify(Json.obj("strategy" -> Strategy.debug.toString())))),
    buildOperation("Set player names", "POST", "/controller/setPlayerNames", StringBody(Json.stringify(Json.obj("playername1" -> "Player1", "playername2" -> "Player2")))),
    buildOperation("Place a card", "POST", "/controller/placeCard", StringBody(Json.stringify(Move(handSlot = 0, fieldSlotActive = 0).toJson))),
    buildOperation("Undo step", "GET", "/controller/undo", StringBody("")),
    buildOperation("Redo step", "GET", "/controller/redo", StringBody("")),
    buildOperation("Save the Game", "GET", "/controller/save", StringBody("")),
  )

  override def executeOperations(): Unit = {
    var scn = buildScenario("Spike Test Scenario - Managable")
    var scn2 = buildScenario("Spike Test Scenario - Overload")

    setUp(
      scn.inject(
        rampUsers(20) during (20.seconds),
        atOnceUsers(1000),
        rampUsers(1000) during (20.seconds),
      ),
      // scn2.inject(
      //   rampUsers(20) during (10.seconds),
      //   atOnceUsers(10000),
      // )
    ).protocols(httpProtocol)
  }

  executeOperations()
}
