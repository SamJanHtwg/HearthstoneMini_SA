package hearthstoneMini
package model

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
        move.p1 should be("player1")
        move.p2 should be("player2")
      }
    }
  }
}