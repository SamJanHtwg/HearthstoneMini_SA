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

class PlaceCardCommandSpec extends AnyWordSpec with Matchers {
  "A controller" should {
    "do step" in {
      val controller = Controller(Field(slotNum = 5, players = List[Player](Player(id = 1)
        .resetAndIncreaseMana(), Player(id = 2))))
      val testField = controller.field
      val placeCardCommand = new PlaceCardCommand(controller, Move(handSlot = 2, fieldSlotActive = 2))
      placeCardCommand.doStep
      placeCardCommand.memento should be (testField)
    }
    "undo step" in {
      val controller = Controller(Field(slotNum = 5, players = List[Player](Player(id = 1)
        .resetAndIncreaseMana(), Player(id = 2))))
      val testField = controller.field
      val placeCardCommand = new PlaceCardCommand(controller, Move(handSlot = 2, fieldSlotActive = 2))
      placeCardCommand.undoStep
      placeCardCommand.memento should be (testField)
    }
    "redo step" in {
      val controller = Controller(Field(slotNum = 5, players = List[Player](Player(id = 1)
        .resetAndIncreaseMana(), Player(id = 2))))
      val testField = controller.field
      val placeCardCommand = new PlaceCardCommand(controller, Move(handSlot = 2, fieldSlotActive = 2))
      placeCardCommand.redoStep
      placeCardCommand.memento should be (testField)
    }

  }
}
