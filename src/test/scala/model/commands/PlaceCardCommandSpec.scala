package hearthstoneMini
package model.commands

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import core.controller.component.controllerImpl.Controller
import _root_.model.cardComponent.cardImpl.Card

import _root_.model.playerComponent.playerImpl.Player
import core.util.Observer
import _root_.model.fieldComponent.FieldInterface
import _root_.model.Move
import _root_.model.fieldComponent.fieldImpl.Field
import core.util.commands.commandImpl.PlaceCardCommand
import org.scalamock.scalatest.MockFactory

class PlaceCardCommandSpec extends AnyWordSpec with Matchers with MockFactory {
  "A controller" should {
    "do step" in {
      val testCards: List[Card] = List[Card](
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, "")
      )
      val controller = mock[Controller]
      val testField = controller.field
      val placeCardCommand = new PlaceCardCommand(
        controller,
        Move(handSlot = 2, fieldSlotActive = 2)
      )
      placeCardCommand.doStep
      placeCardCommand.memento should be(testField)
    }
    "undo step" in {
      val controller = mock[Controller]
      val testField = controller.field
      val placeCardCommand = new PlaceCardCommand(
        controller,
        Move(handSlot = 2, fieldSlotActive = 2)
      )
      placeCardCommand.undoStep
      placeCardCommand.memento should be(testField)
    }
    "redo step" in {
      val controller = mock[Controller]
      val testField = controller.field
      val placeCardCommand = new PlaceCardCommand(
        controller,
        Move(handSlot = 2, fieldSlotActive = 2)
      )
      placeCardCommand.redoStep
      placeCardCommand.memento should be(testField)
    }

  }
}
