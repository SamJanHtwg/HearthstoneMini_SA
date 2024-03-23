package hearthstoneMini
package model.commands

import controller.component.controllerImpl.Controller
import model.Move
import model.fieldComponent.FieldInterface
import util.Command

import scala.util.{Failure, Try, Success}

class DrawCardCommand(controller: Controller) extends Command {
  var memento: FieldInterface = controller.field
  override def doStep: Try[FieldInterface] =
  if checkConditions then {
    memento = controller.field
    Success(controller.field.drawCard())
  } else Failure(Exception("Your hand is full!"))

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

  override def checkConditions: Boolean = controller.field.players.head.gamebar.hand.length < 5
}