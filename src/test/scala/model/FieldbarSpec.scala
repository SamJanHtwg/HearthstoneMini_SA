package hearthstoneMini
package model

import model.cardComponent.cardImpl.Card
import model.fieldbarComponent.fieldbarImpl.Fieldbar

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.fieldComponent.fieldImpl.FieldObject

class FieldbarSpec extends AnyWordSpec with Matchers {
  "A HearthstoneMini fieldbar" when {
    "filled with Card" should {
      val fieldBar1 = new Fieldbar(5, None)
      val fieldBar2 = Fieldbar()
      "be able to place cards" in {
        val newfield = fieldBar1.placeCard(1, new Card("Test", 1, 1, 1, "kann zaubern", "rare"))
        newfield.cardArea.row(1).get shouldBe a[Card]
      }
      "when card is removed" in {
        fieldBar1.removeCard(1).cardArea.slot(1).isDefined should be (false)
      }
    }
    "fieldBar to matrix" in {
      val fieldBarM = Fieldbar().toMatrix.toString() should be ((" " * FieldObject.standartFieldWidth + "\n") * (FieldObject.standartFieldBarHeight - 1)
          + "-" * FieldObject.standartFieldWidth + "\n")
    }
  }
}