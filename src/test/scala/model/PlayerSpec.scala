package hearthstoneMini
package model

import model.gamebarComponent.gamebarImpl.Gamebar
import model.playerComponent.playerImpl.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.cardComponent.cardImpl.Card
import model.fieldbarComponent.fieldbarImpl.Fieldbar

class PlayerSpec extends AnyWordSpec with Matchers {
  val testCards = List[Card](Card("test1", 1, 1, 1, "testEffect1", "testRarety1"),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1"), Card("test1", 1, 1, 1, "testEffect1", "testRarety1"),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1"))

  "A Player" when {
    val player1 = Player(id = 1, gamebar = Gamebar(hand = testCards)).resetAndIncreaseMana()
    val player2 = Player(id = 2)
    "created player with ID = 1" should {
      "have ID 1" in {
        player1.id should be(1)
      }
      "have a gamebar" in {
        player1.gamebar shouldBe a[Gamebar]
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
      "have a gamebar" in {
        player2.gamebar shouldBe a[Gamebar]
      }
      "have a fieldbar" in {
        player2.fieldbar shouldBe a[Fieldbar]
      }
      "have name Player by default" in {
        player2.name should be("Player")
      }
    }
    "placing a card" in {
      player1.placeCard(2,2).fieldbar.cardArea.row(2).isDefined should be(true)
    }
    "drawing a card" in {
      player1.drawCard().gamebar.hand.length.intValue should be (5)
    }
    "destroying a card" in {
      player1.placeCard(2,2).destroyCard(2).gamebar.friedhof.length should be (1)
    }
    "reducing hp" in {
      player1.reduceHp(20).gamebar.hp.value should be (10)
    }
    "increasing hp" in {
      player1.increaseHp(20).gamebar.hp.value should be (50)
    }
    "reducing mana" in {
      player1.reduceMana(10).gamebar.mana.value should be (0)
    }
    "increasing mana" in {
      player1.increaseMana(50).gamebar.mana.value should be (2)
    }
    "reset and increasing mana" in {
      val afterAlter = player1.resetAndIncreaseMana()
      afterAlter.gamebar.mana.value should be (player1.gamebar.mana.value + 1)
      afterAlter.gamebar.mana.max should be (player1.gamebar.mana.max + 1)
    }
    "set a player name" in {
      player1.setName("testName").name should be("testName")
    }
    "set HP value for a Player" in {
      player1.setHpValue(50).gamebar.hp.value should be (50)
    }
    "set Mana for a Player" in {
      player2.setManaValue(40).gamebar.mana.value should be (40)
    }
  }
}