package hearthstoneMini
package model
import _root_.model.cardComponent.cardImpl.Card
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

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
    "cardReads" should {
      "read a Card from JSON" in {
        val json = Json.parse("""
      {
        "type": "MINION",
        "name": "Test Card",
        "cost": 5,
        "attack": 3,
        "health": 2,
        "id": "test-id"
      }
    """)
        val cardOption = Card.cardReads.reads(json).get

        cardOption should not be None

        val card = cardOption.get

        card.name should be("Test Card")
        card.manaCost should be(5)
        card.attValue should be(3)
        card.defenseValue should be(2)
        card.id should be("test-id")
      }
    }
  }
}
