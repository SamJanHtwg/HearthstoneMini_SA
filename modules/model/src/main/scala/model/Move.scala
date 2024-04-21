package model

import play.api.libs.json.*

class Move(
    val handSlot: Int = 0,
    val fieldSlotActive: Int = 0,
    val amount: Int = 0,
    val fieldSlotInactive: Int = 0
) {
    def toJson: JsValue = {
        Json.obj(
            "handSlot" -> handSlot,
            "fieldSlotActive" -> fieldSlotActive,
            "amount" -> amount,
            "fieldSlotInactive" -> fieldSlotInactive
        )
    }
}
