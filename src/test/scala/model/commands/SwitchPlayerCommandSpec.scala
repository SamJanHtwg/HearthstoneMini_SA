package hearthstoneMini
package model.commands

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import core.controller.component.controllerImpl.Controller
import core.model.cardComponent.cardImpl.Card

import core.model.playerComponent.playerImpl.Player
import core.util.Observer
import core.model.fieldComponent.FieldInterface
import core.model.fieldComponent.fieldImpl.Field
import core.util.commands.commandImpl.SwitchPlayerCommand

class SwitchPlayerCommandSpec extends AnyWordSpec with Matchers {
  "A controller" should {
    "when switching players" in {
      val controller = Controller(
        Field(
          players = Map[Int, Player](
            (1, Player(id = 1).resetAndIncreaseMana()),
            (2, Player(id = 2))
          )
        )
      )
      val switchPlayer = new SwitchPlayerCommand(controller)
      val testField = controller.field
      switchPlayer.doStep
      switchPlayer.memento should be(testField)
    }
    "undo / redo step" in {
      val controller = Controller(
        Field(
          players = Map[Int, Player](
            (1, Player(id = 1).resetAndIncreaseMana()),
            (2, Player(id = 2))
          )
        )
      )
      val switchPlayer = new SwitchPlayerCommand(controller)
      val testField = controller.field
      switchPlayer.undoStep
      switchPlayer.memento should be(testField)

      switchPlayer.redoStep
      switchPlayer.memento should be(testField)
    }
  }
}
