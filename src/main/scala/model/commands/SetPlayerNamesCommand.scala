package hearthstoneMini
package model.commands

import controller.component.controllerImpl.Controller
import model.Move
import model.fieldComponent.FieldInterface
import util.Command

import scala.util.{Success, Try}

class SetPlayerNamesCommand(controller: Controller, move: Move) extends Command {
  var memento: FieldInterface = controller.field
  override def doStep: Try[FieldInterface] = {
    memento = controller.field
    Success(controller.field.setPlayerNames(move.p1, move.p2))
  }
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

  override def checkConditions: Boolean = true
}