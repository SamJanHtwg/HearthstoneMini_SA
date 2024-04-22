package core
package util.commands.commandImpl

import controller.component.controllerImpl.Controller
import model.Move
import model.fieldComponent.FieldInterface
import core.util.commands.CommandInterface

import scala.util.{Failure, Success, Try}

class PlaceCardCommand(val field: FieldInterface, move: Move)
    extends CommandInterface {
  var memento: FieldInterface = field
  override def doStep: Try[FieldInterface] =
    Option
      .when(checkConditions) {
        memento = field
        field.placeCard(move.handSlot, move.fieldSlotActive)
      }
      .toRight(Exception("Unable to place a card!"))
      .toTry

  def checkConditions: Boolean = {
    val currentField = field
    val activePlayer =
      currentField.players(currentField.activePlayerId)

    val isSlotEmpty = activePlayer
      .field(move.fieldSlotActive)
      .isEmpty

    val isInRange = move.handSlot < activePlayer.hand.length

    isSlotEmpty
    && isInRange
    && activePlayer.manaValue
      >= activePlayer
        .hand(move.handSlot)
        .manaCost
  }
}
