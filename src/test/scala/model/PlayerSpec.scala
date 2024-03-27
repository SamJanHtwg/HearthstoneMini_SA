package hearthstoneMini
package model

import model.playerComponent.playerImpl.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.cardComponent.cardImpl.Card
import model.fieldbarComponent.fieldbarImpl.Fieldbar

class PlayerSpec extends AnyWordSpec with Matchers {
  val testCards: List[Card] = List[Card](
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, "")
  )

  "A Player" when {
    val player1 = Player(id = 1, hand = testCards).resetAndIncreaseMana()
    val player2 = Player(id = 2)
    "created player with ID = 1" should {
      "have ID 1" in {
        player1.id should be(1)
      }

      "have a fieldbar" in {
        player1.fieldbar shouldBe a[Fieldbar]
      }
      "have name Player by default" in {
        player1.name should be("Player")
      }
    }
    "created player with ID = 2" should {
      "have ID 2" in {
        player2.id should be(2)
      }

      "have a fieldbar" in {
        player2.fieldbar shouldBe a[Fieldbar]
      }
      "have name Player by default" in {
        player2.name should be("Player")
      }
    }
    "placing a card" in {
      player1.placeCard(2, 2).fieldbar.cardArea.row(2).isDefined should be(true)
    }
    "drawing a card" in {
      player1.drawCard().hand.length.intValue should be(5)
    }
    "destroying a card" in {
      player1.placeCard(2, 2).destroyCard(2).friedhof.length should be(1)
    }
    "reducing hp" in {
      player1.reduceHp(20).hpValue should be(10)
    }
    "increasing hp" in {
      player1.increaseHp(20).hpValue should be(50)
    }
    "reducing mana" in {
      player1.reduceMana(10).manaValue should be(0)
    }
    "increasing mana" in {
      player1.increaseMana(50).manaValue should be(2)
    }
    "reset and increasing mana" in {
      val afterAlter = player1.resetAndIncreaseMana()
      afterAlter.manaValue should be(player1.manaValue + 1)
      afterAlter.maxManaValue should be(player1.maxManaValue + 1)
    }
    "set a player name" in {
      player1.setName("testName").name should be("testName")
    }
    "set HP value for a Player" in {
      player1.setHpValue(50).hpValue should be(50)
    }
    "set Mana for a Player" in {
      player2.setManaValue(40).manaValue should be(40)
    }
  }
}
