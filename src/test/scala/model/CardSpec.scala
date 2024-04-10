package hearthstoneMini
package model
import model.cardComponent.cardImpl.Card
import model.matrixComponent.matrixImpl.Matrix
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec with Matchers {
  "A Card" when {
    "filled" should {
      val card = new Card("test", 2, 2, 2, "Schmettern", "rare", 0, "")
      "have a String repesentation" in {
        card.toString() should be("test (2)#atk: 2#def: 2#Schmettern#rare")
      }
      "have a name" in {
        card.name should be("test")
      }
      "have Manacost" in {
        card.manaCost should be(2)
      }
      "have atk Value" in {

        card.attValue should be(2)
      }
      "have def Value" in {
        card.defenseValue should be(2)
      }
      "have an effekt" in {
        card.effect should be("Schmettern")
      }
      "have a Rarity" in {
        card.rarity should be("rare")
      }
    }
  }
}
