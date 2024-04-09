package hearthstoneMini
package model

import model.playerComponent.playerImpl.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.cardComponent.cardImpl.Card
import hearthstoneMini.model.cardComponent.CardInterface

class PlayerSpec extends AnyWordSpec with Matchers {
  val testCards: List[Card] = List(
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test2", 2, 2, 2, "testEffect2", "testRarety2", 0, ""),
    Card("test3", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test4", 3, 3, 3, "testEffect3", "testRarety3", 0, ""),
    Card("test5", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test6", 4, 4, 4, "testEffect4", "testRarety4", 0, "")
  )

  val player1: Player = Player("Player1", 1)
  val player2: Player = Player("Player2", 2)

  "A Player" when {
    "created should have default values" in {
      player1.name should be("Player1")
      player1.id should be(1)
      player1.hpValue should be(1)
      player1.maxHpValue should be(5)
      player1.manaValue should be(1)
      player1.maxManaValue should be(2)
      player1.hand.length should be(5)
      player1.deck.length should be(30)
      player1.field shouldBe a[Vector[Option[CardInterface]]]
    }
    "set a name" in {
      val player = player2.setName("TestPlayer2")
      player.name should be("TestPlayer2")
    }
  }

  "Player actions" when {
    "placing a card" in {
      val initialHandSize = player1.hand.size
      val newPlayer = player1.placeCard(2, 2)
      newPlayer.field(2).isDefined should be(true)
      newPlayer.hand.size should be(initialHandSize - 1)
    }
    "drawing a card" in {
      val initialHandSize = player1.hand.size
      val initialDeckSize = player1.deck.size
      val newPlayer = player1.drawCard()
      newPlayer.hand.size should be(initialHandSize + 1)
      newPlayer.deck.size should be(initialDeckSize - 1)
    }
    "reduce & increase hp" in {
      val player = player1.setHpValue(10)
      val newPlayer = player.reduceHp(4).increaseHp(2)
      newPlayer.hpValue should be(8)
    }
    "if hp is zero" in {
      val player = player1.setHpValue(0)
      player.isHpEmpty should be(true)
    }
    "reduce & increase mana" in {
      val player = player1.setManaValue(10)
      val newPlayer = player.reduceMana(5).increaseMana(2)
      newPlayer.manaValue should be(7)
    }
    "reset & increase mana" in {
      val player = player1.setManaValue(10).resetAndIncreaseMana()
      player.manaValue should be(11)
      player.maxManaValue should be(11)
    }
    "if mana is zero" in {
      val player = player1.setManaValue(0)
      player.isManaEmpty should be(true)
    }
  }
}
