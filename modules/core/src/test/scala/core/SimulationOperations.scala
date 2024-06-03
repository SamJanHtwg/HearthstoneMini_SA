package core

import core.controller.Strategy
import io.gatling.core.Predef.*
import io.gatling.core.body.Body
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.*
import model.GameState
import model.Move
import play.api.libs.json.Json

val operations: List[ChainBuilder] = List(
  buildOperation(
    "Reset",
    "POST",
    "/controller/setGameState",
    StringBody(
      Json.stringify(Json.obj("gameState" -> GameState.MAINGAME.toString()))
    )
  ),
  buildOperation(
    "Reset the Game",
    "GET",
    "/controller/delete",
    StringBody("")
  ),
  buildOperation(
    "Start a new Game",
    "POST",
    "/controller/setStrategy",
    StringBody(
      Json.stringify(Json.obj("strategy" -> Strategy.debug.toString()))
    )
  ),
  buildOperation(
    "Set player names",
    "POST",
    "/controller/setPlayerNames",
    StringBody(
      Json.stringify(
        Json.obj("playername1" -> "Player1", "playername2" -> "Player2")
      )
    )
  ),
  buildOperation(
    "Place a card",
    "POST",
    "/controller/placeCard",
    StringBody(Json.stringify(Move(handSlot = 0, fieldSlotActive = 0).toJson))
  ),
  buildOperation("Undo step", "GET", "/controller/undo", StringBody("")),
  buildOperation("Redo step", "GET", "/controller/redo", StringBody("")),
  buildOperation("Save the Game", "GET", "/controller/save", StringBody(""))
)

def buildOperation(
    name: String,
    request: String,
    operation: String,
    body: Body
): ChainBuilder = {
  exec(
    http(name)
      .httpRequest(request, operation)
      .body(body)
  )
}
