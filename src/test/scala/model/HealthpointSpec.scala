
package hearthstoneMini
package model

import model.healthpointsComponent.healthpointsImpl.Healthpoints
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class HpSpec extends AnyWordSpec with Matchers {
  "HP" when {
    "not set any value " should {
      "have Value 0" in {
        val emptyHp = Healthpoints()
        emptyHp.value should be(0)
        emptyHp.max should be(30)
      }
      "not be set" in {
        val emptyHp = Healthpoints()
        emptyHp.isEmpty should be(true)
      }
    }
  }
  "HP decrease" when {
    "initialized with 100" should {
      val setHp = Healthpoints(100, 100)
      "have Value 100" in {
        setHp.value should be(100)
        setHp.max should be(100)
      }
      "have decreased value 90" in {
        val afterAlter = setHp.decrease(10)
        afterAlter.value should be(90)
        afterAlter.max should be(100)
        afterAlter.decrease(100).value should be(0)
      }
    }
  }
  "HP increase" when {
    "initialized with 100" should {
      val setHp = Healthpoints(100, 100)
      "have Value 100" in {
        setHp.value should be(100)
      }
      "have increased value 110" in {
        val afterAlter = setHp.increase(10)
        afterAlter.value should be(110)
        afterAlter.max should be(110)
      }
    }
  }
  "set value for Hp" in {
    Healthpoints().setVal(20).value should be(20)
  }
  "when empty" in {
    Healthpoints().setVal(0).isEmpty should be (true)
  }
  "when increased" in {
    Healthpoints().setVal(40).increase(20).value should be (60)
  }

}