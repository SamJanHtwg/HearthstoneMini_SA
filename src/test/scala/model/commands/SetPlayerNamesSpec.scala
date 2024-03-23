package hearthstoneMini
package model.commands

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import controller.component.controllerImpl.Controller
import model.cardComponent.cardImpl.{Card, EmptyCard}

import model.playerComponent.playerImpl.Player
import util.Observer
import model.fieldComponent.FieldInterface
import model.Move
import model.fieldComponent.fieldImpl.Field

class SetPlayerNamesSpec extends AnyWordSpec with Matchers {
  "A controller" should {
    "when setting names" in {
      val controller = Controller(Field(slotNum = 5, players = List[Player](Player(id = 1)
        .resetAndIncreaseMana(), Player(id = 2))))
      val setPlayerNames = new SetPlayerNamesCommand(controller, Move(p1 = "Sam", p2 = "Jan"))
      val testField = controller.field
      setPlayerNames.doStep
      setPlayerNames.memento should be (testField)
    }
    "undo / redo step" in {
      val controller = Controller(Field(slotNum = 5, players = List[Player](Player(id = 1)
        .resetAndIncreaseMana(), Player(id = 2))))
      val setPlayerNames = new SetPlayerNamesCommand(controller, Move(p1 = "Sam", p2 = "Jan"))
      val testField = controller.field
      setPlayerNames.undoStep
      setPlayerNames.memento should be (testField)

      setPlayerNames.redoStep
      setPlayerNames.memento should be (testField)
    }
  }
}
