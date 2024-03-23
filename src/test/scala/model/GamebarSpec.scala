package hearthstoneMini
package model

import model.gamebarComponent.gamebarImpl.Gamebar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.cardComponent.cardImpl.Card

class GamebarSpec extends AnyWordSpec with Matchers {

  val testCards: List[Card] = List[Card](Card("test1", 1, 1, 1, "testEffect1", "testRarety1"),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1"), Card("test1", 1, 1, 1, "testEffect1", "testRarety1"),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1"))

  "A Gamebar" when {
    val gameBar = Gamebar(hand = testCards)
    val gameBar1 = Gamebar()
    "created" should {
      "be created in initial state" in {
        gameBar.hand.count(_.isInstanceOf[Card]) should be(4)
      }
    }
    "you draw a card" in {
      gameBar.drawCard().hand.length should be (5)
    }
    "a card is removed from hand" in {
      gameBar1.removeCardFromHand(1).hand.size should be(4)
    }
    "a card is added to hand" in {
      val card = Card("test", 2, 2, 2, "Schmettern", "rare")
      gameBar.addCardToHand(Some(card)).hand.length should be(5)
    }
    "hp reduced by 20" in {
      gameBar.reduceHp(20).hp.value should be (10)
    }
    "hp increased by 40" in {
      gameBar.increaseHp(40).hp.value should be (70)
    }
    "mana reduced by 10" in {
      gameBar.reduceMana(10).mana.value should be (0)
    }
    "mana increased" in {
      gameBar.increaseMana(10).mana.value should be (1)
    }
    "card added to Friedhof" in {
      val card = Card("test", 2, 2, 2, "Schmettern", "rare")
      gameBar.addCardToFriedhof(Some(card)).friedhof.length should be (1)
    }
    "reset and increased mana" in {
      val afterAlter = gameBar.resetAndIncreaseMana()
      afterAlter.mana.value should be(gameBar.mana.value + 1)
      afterAlter.mana.max should be(gameBar.mana.max + 1)
    }
  }
}

