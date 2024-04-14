package hearthstoneMini
package model
import core.model.cardComponent.cardImpl.Card
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec with Matchers {
  "A Card" when {
    "filled" should {
      val card = new Card("test", 2, 2, 2, "Schmettern", "rare", 0, "")
      "have a String representation" in {
        card.toString() should be("test (2)#atk: 2#def: 2#Schmettern#rare")
      }
      "have a name" in {
        card.name should be("test")
      }
      "have Mana cost" in {
        card.manaCost should be(2)
      }
      "have atk Value" in {

        card.attValue should be(2)
      }
      "have def Value" in {
        card.defenseValue should be(2)
      }
      "have an effect" in {
        card.effect should be("Schmettern")
      }
      "have a Rarity" in {
        card.rarity should be("rare")
      }
    }
  }
}
