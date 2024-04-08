package hearthstoneMini
package util.commands.commandImpl

import controller.component.controllerImpl.Controller
import model.Move
import model.fieldComponent.FieldInterface
import hearthstoneMini.util.commands.CommandInterface

import scala.util.{Failure, Success, Try}
import hearthstoneMini.controller.component.ControllerInterface

class PlaceCardCommand(controller: ControllerInterface, move: Move)
    extends CommandInterface {
  var memento: FieldInterface = controller.field
  override def doStep: Try[FieldInterface] =
    Option
      .when(checkConditions) {
        memento = controller.field
        controller.field.placeCard(move.handSlot, move.fieldSlotActive)
      }
      .toRight(Exception("Unable to place a card!"))
      .toTry

  override def undoStep: Unit = {
    val new_memento = controller.field
    controller.field = memento
    memento = new_memento
  }

  override def redoStep: Unit = {
    val new_memento = controller.field
    controller.field = memento
    memento = new_memento
  }

  def checkConditions: Boolean = {
    val currentField = controller.field
    val activePlayer =
      currentField.players(currentField.activePlayerId)

    val isSlotEmpty = activePlayer
      .field(move.fieldSlotActive)
      .isEmpty

    val isInRange = move.handSlot < activePlayer.hand.length

    val hasMana = activePlayer.manaValue
      >= activePlayer
        .hand(move.handSlot)
        .manaCost

    isSlotEmpty
    && isInRange
    && hasMana
  }
}
