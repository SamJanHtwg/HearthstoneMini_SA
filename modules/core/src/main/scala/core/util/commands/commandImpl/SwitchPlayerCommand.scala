package core
package util.commands.commandImpl

import model.Move
import model.fieldComponent.FieldInterface
import core.util.commands.CommandInterface
import scala.util.{Success, Try}

class SwitchPlayerCommand(val field: FieldInterface) extends CommandInterface {
  var memento: FieldInterface = field

  override def doStep: Try[FieldInterface] = {
    memento = field
    Success(field.switchPlayer().resetAttackCount())
  }
}
