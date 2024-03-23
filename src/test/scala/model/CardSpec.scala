package hearthstoneMini
package model
import model.cardComponent.cardImpl.Card
import model.cardComponent.cardImpl.EmptyCard
import model.matrixComponent.matrixImpl.Matrix
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec with Matchers {
   "A Card" when {
    "filled" should {
      val card = new Card("test", 2, 2, 2, "Schmettern", "rare")
      "have a String repesentation" in {
        card.toString() should be("test (2)#atk: 2#def: 2#Schmettern#rare")
      }
      "have a Matrix repesentation" in {
        card.toMatrix.toString should be("test (2)       \natk: 2         \ndef: 2         \nSchmettern     \nrare           \n")
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

     "a empty Card when init" should  {
       val emptyCard = new EmptyCard(defenseValue = 5, attackCount = 3)

       "have standard values" in {
         emptyCard.name should be ("yolo")
         emptyCard.manaCost should be (0)
         emptyCard.attValue should be (0)
         emptyCard.defenseValue should be (5)
         emptyCard.effect should be ("")
         emptyCard.rarity should be ("")
         emptyCard.attackCount should be (3)
       }

       "has a matrix representation" in {
         emptyCard.toMatrix.colSize should be (15)
         emptyCard.toMatrix.rowSize should be (5)
       }

       "should can be attacked" in {
         emptyCard.reduceHP(4).defenseValue should be (1)
       }

       "should can reduce and reset attack count" in {
         emptyCard.reduceAttackCount().attackCount should be (2)
         emptyCard.resetAttackCount().resetAttackCount().attackCount should be (0)
       }
     }
  }
}