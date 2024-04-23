package hearthstoneMini
package model

import _root_.model.Move
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MoveSpec extends AnyWordSpec with Matchers {
  "A Move" when {
    "Created" should {
      "be created with Empty constructor and have default values" in {
        val move = Move()
        move.amount should be(0)
        move.handSlot should be(0)
        move.fieldSlotActive should be(0)
        move.fieldSlotInactive should be(0)
      }
    }
    "serialized" should {
      "have same values after serialization" in {
        val move = Move(1, 2, 3, 4)
        val toJson = move.toJson
        val fromJson = Move.fromJson(toJson)

        fromJson.amount should be(move.amount)
        fromJson.handSlot should be(move.handSlot)
        fromJson.fieldSlotActive should be(move.fieldSlotActive)
        fromJson.fieldSlotInactive should be(move.fieldSlotInactive)
      }
    }

  }
}
