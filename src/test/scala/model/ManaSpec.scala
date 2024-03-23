import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import hearthstoneMini.model.manaComponent.manaImpl.Mana


class ManaSpec extends AnyWordSpec with Matchers {
  val testMana = Mana(50,100)
  "Mana" when {
   "Mana Set" when { 
      "value is set" should {
        "set to new value" in {
          testMana.setVal(40).value should be (40)
        }
      }
    }
    "Mana decrease" when {
      "initialized with 100" should {
        val setMana = Mana(100, 100)
        "have Value 100" in {
          setMana.value should be(100)
          setMana.max should be(100)
        }
        "have decreased value 0" in {
          val afterAlter = setMana.decrease(100)
          afterAlter.value should be(0)
          afterAlter.max should be(100)
          afterAlter.isEmpty should be(true)
        }
      }
    }
    "Mana increase" when {
      "initialized with 100" should {
        val setMana = Mana(100, 100)
        "have Value 100" in {
          setMana.value should be(100)
          setMana.max should be(100)
        }
        "have increased value 110" in {
          val afterAlter = setMana.increase(10)
          afterAlter.value should be(100)
          afterAlter.max should be(100)
        }
        "have increased by 1 and reset" in {
          val afterAlter = setMana.resetAndIncrease()
          afterAlter.max should be(setMana.max + 1)
          afterAlter.value should be(setMana.max + 1)
        }
        "when increased" in {
          testMana.increase(20).value should be (70)
        }
      }
    }
    "when is empty" should {
      "be empty" in {
        testMana.setVal(0).isEmpty should be (true)
      }
    } 
  }
}