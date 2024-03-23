package hearthstoneMini
package aview

import controller.component.controllerImpl.Controller
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import util.Observer
import scala.util.Try
import scala.util.Success
import model.fieldComponent.fieldImpl.Field


class StrategySpec extends AnyWordSpec with Matchers {
  "The Tui" when {
    val tui = new Tui(Controller(Field()))
    "checkInput is used" should {
      "return a Try" in {
        tui.checkInput("1")should be (true)
      }
    }
  }
}

