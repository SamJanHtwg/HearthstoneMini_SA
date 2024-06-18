package core.controller

import play.api.libs.json._
import core.controller._
import scalafx.scene.input.KeyCode.P

trait ServiceMessage {
  val id: String
  val data: Option[JsValue]
}

object ServiceMessage {
  given serviceMessageWrites: Writes[ServiceMessage] = (o: ServiceMessage) =>
    Json.obj(
      "type" -> o.getClass.getSimpleName.replace("$", ""),
      "data" -> o.data,
      "id" -> o.id
    )

  given serviceMessageReads: Reads[ServiceMessage] = (o: JsValue) => {
    (o \ "type").validate[String] match
      case JsSuccess("GetFieldMessage", _) =>
        JsSuccess(
          GetFieldMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("UpdateFieldMessage", _) =>
        JsSuccess(
          UpdateFieldMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("DeleteMessage", _) =>
        JsSuccess(
          DeleteMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("DrawCardMessage", _) =>
        JsSuccess(
          DrawCardMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("SwitchPlayerMessage", _) =>
        JsSuccess(
          SwitchPlayerMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("CanUndoMessage", _) =>
        JsSuccess(
          CanUndoMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("CanUndoResponeMessage", _) =>
        JsSuccess(
          CanUndoResponeMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("CanRedoMessage", _) =>
        JsSuccess(
          CanRedoMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("CanRedoResponeMessage", _) =>
        JsSuccess(
          CanRedoResponeMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("UndoMessage", _) =>
        JsSuccess(
          UndoMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("RedoMessage", _) =>
        JsSuccess(
          RedoMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("PlaceCardMessage", _) =>
        JsSuccess(
          PlaceCardMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("SetPlayerNamesMessage", _) =>
        JsSuccess(
          SetPlayerNamesMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("SetGameStateMessage", _) =>
        JsSuccess(
          SetGameStateMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("SetStrategyMessage", _) =>
        JsSuccess(
          SetStrategyMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("AttackMessage", _) =>
        JsSuccess(
          AttackMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("DirectAttackMessage", _) =>
        JsSuccess(
          DirectAttackMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case JsSuccess("EndTurnMessage", _) =>
        JsSuccess(
          EndTurnMessage(
            data = (o \ "data").asOpt[JsValue],
            id = (o \ "id").as[String]
          )
        )
      case _ => JsSuccess(GetFieldMessage(id = "unknown"))
  }
}
case class GetFieldMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object GetFieldMessage {
  given getFieldMessageWrites: Writes[GetFieldMessage] = (o: GetFieldMessage) =>
    Json.obj(
      "type" -> o.getClass.getSimpleName.replace("$", ""),
      "data" -> o.data,
      "id" -> o.id
    )
}
case class UpdateFieldMessage(data: Option[JsValue], id: String)
    extends ServiceMessage

object UpdateFieldMessage {
  given updateFieldMessageWrites: Writes[UpdateFieldMessage] =
    (o: UpdateFieldMessage) =>
      Json.obj(
        "type" -> o.getClass.getSimpleName.replace("$", ""),
        "data" -> o.data,
        "id" -> o.id
      )
}

case class DeleteMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object DeleteMessage {
  given deleteMessageWrites: Writes[DeleteMessage] = (o: DeleteMessage) =>
    Json.obj(
      "type" -> o.getClass.getSimpleName.replace("$", ""),
      "data" -> o.data,
      "id" -> o.id
    )
}

case class DrawCardMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object DrawCardMessage {
  given drawCardMessageWrites: Writes[DrawCardMessage] = (o: DrawCardMessage) =>
    Json.obj(
      "type" -> o.getClass.getSimpleName.replace("$", ""),
      "data" -> o.data,
      "id" -> o.id
    )
}

case class SwitchPlayerMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object SwitchPlayerMessage {
  given switchPlayerMessageWrites: Writes[SwitchPlayerMessage] =
    (o: SwitchPlayerMessage) =>
      Json.obj(
        "type" -> o.getClass.getSimpleName.replace("$", ""),
        "data" -> o.data,
        "id" -> o.id
      )
}

case class CanUndoMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object CanUndoMessage {
  given canUndoMessageWrites: Writes[CanUndoMessage] = (o: CanUndoMessage) =>
    Json.obj(
      "type" -> o.getClass.getSimpleName.replace("$", ""),
      "data" -> o.data,
      "id" -> o.id
    )
}
case class CanUndoResponeMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object CanUndoResponeMessage {
  given canUndoResponeMessageWrites: Writes[CanUndoResponeMessage] =
    (o: CanUndoResponeMessage) =>
      Json.obj(
        "type" -> o.getClass.getSimpleName.replace("$", ""),
        "data" -> o.data,
        "id" -> o.id
      )
}
case class CanRedoMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object CanRedoMessage {
  given canRedoMessageWrites: Writes[CanRedoMessage] = (o: CanRedoMessage) =>
    Json.obj(
      "type" -> o.getClass.getSimpleName.replace("$", ""),
      "data" -> o.data,
      "id" -> o.id
    )
}

case class CanRedoResponeMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object CanRedoResponeMessage {
  given canRedoResponeMessageWrites: Writes[CanRedoResponeMessage] =
    (o: CanRedoResponeMessage) =>
      Json.obj(
        "type" -> o.getClass.getSimpleName.replace("$", ""),
        "data" -> o.data,
        "id" -> o.id
      )
}

case class UndoMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object UndoMessage {
  given undoMessageWrites: Writes[UndoMessage] = (o: UndoMessage) =>
    Json.obj(
      "type" -> o.getClass.getSimpleName.replace("$", ""),
      "data" -> o.data,
      "id" -> o.id
    )
}
case class RedoMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object RedoMessage {
  given redoMessageWrites: Writes[RedoMessage] = (o: RedoMessage) =>
    Json.obj(
      "type" -> o.getClass.getSimpleName.replace("$", ""),
      "data" -> o.data,
      "id" -> o.id
    )
}
case class PlaceCardMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object PlaceCardMessage {
  given placeCardMessageWrites: Writes[PlaceCardMessage] =
    (o: PlaceCardMessage) =>
      Json.obj(
        "type" -> o.getClass.getSimpleName.replace("$", ""),
        "data" -> o.data,
        "id" -> o.id
      )
}
case class SetPlayerNamesMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object SetPlayerNamesMessage {
  given setPlayerNamesMessageWrites: Writes[SetPlayerNamesMessage] =
    (o: SetPlayerNamesMessage) =>
      Json.obj(
        "type" -> o.getClass.getSimpleName.replace("$", ""),
        "data" -> o.data,
        "id" -> o.id
      )
}
case class SetGameStateMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object SetGameStateMessage {
  given setGameStateMessageWrites: Writes[SetGameStateMessage] =
    (o: SetGameStateMessage) =>
      Json.obj(
        "type" -> o.getClass.getSimpleName.replace("$", ""),
        "data" -> o.data,
        "id" -> o.id
      )
}
case class SetStrategyMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object SetStrategyMessage {
  given setStrategyMessageWrites: Writes[SetStrategyMessage] =
    (o: SetStrategyMessage) =>
      Json.obj(
        "type" -> o.getClass.getSimpleName.replace("$", ""),
        "data" -> o.data,
        "id" -> o.id
      )
}
case class AttackMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object AttackMessage {
  given attackMessageWrites: Writes[AttackMessage] = (o: AttackMessage) =>
    Json.obj(
      "type" -> o.getClass.getSimpleName.replace("$", ""),
      "data" -> o.data,
      "id" -> o.id
    )
}
case class DirectAttackMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object DirectAttackMessage {
  given directAttackMessageWrites: Writes[DirectAttackMessage] =
    (o: DirectAttackMessage) =>
      Json.obj(
        "type" -> o.getClass.getSimpleName.replace("$", ""),
        "data" -> o.data,
        "id" -> o.id
      )
}
case class EndTurnMessage(data: Option[JsValue] = None, id: String)
    extends ServiceMessage

object EndTurnMessage {
  given endTurnMessageWrites: Writes[EndTurnMessage] = (o: EndTurnMessage) =>
    Json.obj(
      "type" -> o.getClass.getSimpleName.replace("$", ""),
      "data" -> o.data,
      "id" -> o.id
    )
}
