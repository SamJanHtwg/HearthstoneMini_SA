package hearthstoneMini
package model.commands

import controller.component.controllerImpl.Controller
import model.Move
import model.fieldComponent.FieldInterface
import util.Command

import scala.util.{Failure, Success, Try}
import hearthstoneMini.controller.component.ControllerInterface

class PlaceCardCommand(controller: ControllerInterface, move: Move) extends Command {
  var memento: FieldInterface = controller.field
  override def doStep: Try[FieldInterface] = if checkConditions then {
    memento = controller.field
    Success(controller.field.placeCard(move.handSlot, move.fieldSlotActive))
  } else Failure(Exception("Unable to place a card!"))

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

  override def checkConditions: Boolean = controller.field.players.head.fieldbar.cardArea
      .slot(move.fieldSlotActive).isEmpty
    && move.handSlot < controller.field.players.head.gamebar.hand.length
    && controller.field.players.head.gamebar.mana.value
      >= controller.field.players.head.gamebar.hand(move.handSlot).manaCost
}