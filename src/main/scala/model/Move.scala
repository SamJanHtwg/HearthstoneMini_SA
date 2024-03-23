package hearthstoneMini
package model

import scala.annotation.meta.field

case class Move(handSlot: Int = 0, fieldSlotActive: Int = 0, amount: Int = 0, p1: String = "player1", p2: String = "player2", fieldSlotInactive: Int = 0)
