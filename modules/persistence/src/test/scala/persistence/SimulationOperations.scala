package persistence

import io.gatling.core.Predef.*
import io.gatling.core.body.Body
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.*
import model.GameState
import model.Move
import play.api.libs.json.Json
import scalafx.scene.input.KeyCode.E

val operationList: List[ChainBuilder] = List(
  buildOperation(
    "Save",
    "POST",
    "/persistence/save",
    ElFileBody("./resources/field.json")
  ),
  buildOperation(
    "Update",
    "POST",
    "/persistence/update",
    ElFileBody("./resources/field.json")
  ),
  buildOperation(
    "Load",
    "GET",
    "/persistence/load",
    StringBody("")
  ),
  buildOperation(
    "Delete",
    "GET",
    "/persistence/delete",
    StringBody("")
  )
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
