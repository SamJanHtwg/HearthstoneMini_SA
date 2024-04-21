package core
package util.commands.commandImpl

import model.Move
import model.fieldComponent.FieldInterface
import core.util.commands.CommandInterface
import scala.util.{Success, Try}
import core.controller.component.ControllerInterface

class SwitchPlayerCommand(controller: ControllerInterface)
    extends CommandInterface {
  var memento: FieldInterface = controller.field
  override def doStep: Try[FieldInterface] = {
    memento = controller.field
    Success(controller.field.switchPlayer().resetAttackCount())
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
}
