package hearthstoneMini
package model

import model.cardComponent.cardImpl.Card
import model.fieldComponent.fieldImpl
import model.playerComponent.playerImpl
import model.playerComponent.playerImpl.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.fieldComponent.fieldImpl.Field
import model.fieldComponent.fieldImpl.Field

class FieldSpec extends AnyWordSpec with Matchers {
  "A Field" when {
    "created" should {
      val testCards = List[Card](
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, "")
      )

      val field0 = new Field("Player1", "Player2")
      var field = new Field(
        players = Map[Int, Player](
          (1, Player(id = 1, hand = testCards).resetAndIncreaseMana()),
          (2, Player(id = 2))
        )
      )

      val field1 = new Field()
      "be created with empty constructor" in {
        val field0 = Field()

      }
      "have a Card in slot 1 after placed 1 card in slot 1 from hand" in {
        field0
          .placeCard(0, 0)
          .players(field.activePlayerId)
          .hand
          .length should be(4)
        field0
          .placeCard(0, 0)
          .players(field.activePlayerId)
          .field(0)
          .isDefined should be(true)
      }
      "have no Card in slot 1 after the card was removed" in {
        val field1 = field.placeCard(0, 0)
        field1
          .destroyCard(1, 0)
          .players(field.activePlayerId)
          .field(0)
          .isDefined should be(false)
      }
      "have 5 cards in hand after drawing 1 form deck" in {
        field.drawCard().players(field.activePlayerId).hand.length should be(6)
      }
      "have 4 Hp when reduced by 1" in {
        field = field.copy(players =
          field.players
            .updated(1, field.players(1).copy(hpValue = 5, maxHpValue = 25))
        )
        field.reduceHp(1, 1).players(field.activePlayerId).hpValue should be(
          4
        )
      }
      "have 25 Hp when increased by 20" in {
        field = field.copy(players =
          field.players
            .updated(1, field.players(1).copy(hpValue = 5, maxHpValue = 25))
        )
        field.increaseHp(20).players(field.activePlayerId).hpValue should be(25)
      }
      "have 0 Mana when reduced by 10" in {
        field.reduceMana(10).players(field.activePlayerId).manaValue should be(
          0
        )
      }
      "have 1 Mana when increased" in {
        field = field.copy(players =
          field.players
            .updated(1, field.players(1).copy(manaValue = 1, maxManaValue = 2))
        )
        field.increaseMana(20).players(1).manaValue should be(2)
      }
      "switch the active player" in {
        val fieldAfterMove = field.switchPlayer()
        fieldAfterMove.activePlayerId should be(
          field.getInactivePlayerId
        )
        val fieldAfter2ndMove = fieldAfterMove.switchPlayer()
        fieldAfter2ndMove.players(field.activePlayerId).manaValue should be(3)
      }
      "return the active player" in {
        field.getActivePlayer should be(field.players(field.activePlayerId))
      }
      "return player with id 1" in {
        field.getPlayerById(1) should be(field.players(field.activePlayerId))
      }
      "have reset and increased mana" in {
        val fieldAfterMove = field1.resetAndIncreaseMana()
        fieldAfterMove.players(field.activePlayerId).manaValue should be(2)
        fieldAfterMove.players(field.activePlayerId).maxManaValue should be(2)
        fieldAfterMove.players(field.getInactivePlayerId).manaValue should be(2)
        fieldAfterMove
          .players(field.getInactivePlayerId)
          .maxManaValue should be(2)
      }
    }
    "when hp value is set" in {
      new Field("1", "2").setHpValues(34).players.head._2.hpValue should be(
        34
      )
    }
    "when mana value is set" in {
      new Field("1", "2").setManaValues(45).players(1).manaValue should be(
        45
      )
    }
    "restored from xml" in {
      val field = new Field("1", "2")
      val xml = field.toXML
      val fromXml = Field.fromXml(xml).toXML
      assert(xml == fromXml)
    }
  }
}
