
package hearthstoneMini
package model.commands

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import controller.component.controllerImpl.Controller
import model.cardComponent.cardImpl.{Card, EmptyCard}

import model.playerComponent.playerImpl.Player
import util.Observer
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field

class SwitchPlayerSpec extends AnyWordSpec with Matchers {
  "A controller" should {
    "when switching players" in {
      val controller = Controller(Field(slotNum = 5, players = List[Player](Player(id = 1)
        .resetAndIncreaseMana(), Player(id = 2))))
      val switchPlayer = new SwitchPlayerCommand(controller)
      val testField = controller.field
      switchPlayer.doStep
      switchPlayer.memento should be (testField)
    }
    "undo / redo step" in {
      val controller = Controller(Field(slotNum = 5, players = List[Player](Player(id = 1)
        .resetAndIncreaseMana(), Player(id = 2))))
      val switchPlayer = new SwitchPlayerCommand(controller)
      val testField = controller.field
      switchPlayer.undoStep
      switchPlayer.memento should be (testField)

      switchPlayer.redoStep
      switchPlayer.memento should be (testField)
    }
  }
}
