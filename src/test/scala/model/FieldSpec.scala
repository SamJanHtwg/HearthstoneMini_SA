package hearthstoneMini
package model

import model.cardComponent.cardImpl.Card
import model.fieldComponent.fieldImpl
import model.gamebarComponent.gamebarImpl.Gamebar
import model.playerComponent.playerImpl
import model.playerComponent.playerImpl.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.fieldComponent.fieldImpl.Field
import model.fieldComponent.fieldImpl.FieldObject

class FieldSpec extends AnyWordSpec with Matchers {
  "A Field" when {
    "created" should {
      val testCards = List[Card](Card("test1", 1, 1, 1, "testEffect1", "testRarety1"),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1"), Card("test1", 1, 1, 1, "testEffect1", "testRarety1"),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1"), Card("test1", 1, 1, 1, "testEffect1", "testRarety1"))

      val field0 = new Field(5, "Player1", "Player2")
      val field = new Field(slotNum = 5, players = List[Player](
        playerImpl.Player(id = 1, gamebar = Gamebar(hand = testCards)).resetAndIncreaseMana(),
        Player(id = 2)))

      val field1 = new Field(5)
      "be created with empty constructor" in {
        val field0 = Field()
        field0.slotNum should be(5)
      }
      "be created using default fieldsize 5 and 2 player names" in {
        field.matrix.colSize should be(FieldObject.standartFieldWidth)
      }
      "have a Card in slot 1 after placed 1 card in slot 1 from hand" in {
        field0.placeCard(0, 0).players(0).gamebar.hand.length should be(4)
        field0.placeCard(0, 0).players(0).fieldbar.cardArea.row(0).isDefined should be(true)
      }
      "have no Card in slot 1 when remove a card in slot 1" in {
        val field1 = field.placeCard(0, 0)
        field1.destroyCard(0, 0).players(0).fieldbar.cardArea.row(0).isDefined should be (false)

      }
      "have 5 cards in hand after drawing 1 form deck" in {
        field.drawCard().players(0).gamebar.hand.length should be(6)
      }
      "have 10 Hp when reduced by 20" in {
        field.reduceHp(0, 20).players(0).gamebar.hp.value should be(10)
      }
      "have 50 Hp when increased by 20" in {
        field.increaseHp(20).players(0).gamebar.hp.value should be(50)
      }
      "have 0 Mana when reduced by 10" in {
        field.reduceMana(10).players(0).gamebar.mana.value should be(0)
      }
      "have 1 Mana when increased" in {
        field.increaseMana(20).players(1).gamebar.mana.value should be(1)
      }
      "switch the active player" in {
        val fieldAfterMove = field.switchPlayer()
        fieldAfterMove.players should be(field.players.reverse)
        val fieldAfter2ndMove = fieldAfterMove.switchPlayer()
        fieldAfter2ndMove.players(0).gamebar.mana.value should be(3)
      }
      "return the active player" in {
        field.getActivePlayer should be(field.players(0))
      }
      "return player with id 1" in {
        field.getPlayerById(1) should be(field.players(0))
      }
      "have a Matrix representation" in {
        field1.toMatrix.colSize should be(85)
        field1.toMatrix.rowSize should be(31)


      }
      "have reset and increased mana" in {
        val fieldAfterMove = field1.resetAndIncreaseMana()
        fieldAfterMove.players(0).gamebar.mana.value should be(2)
        fieldAfterMove.players(0).gamebar.mana.max should be(2)
        fieldAfterMove.players(1).gamebar.mana.value should be(2)
        fieldAfterMove.players(1).gamebar.mana.max should be(2)
      }
    }
     "when hp value is set" in {
       Field().setHpValues(34).players(0).gamebar.hp.value should be (34)
     }
    "when mana value is set" in {
      Field().setManaValues(45).players(1).gamebar.mana.value should be (45)
    }
  }
}