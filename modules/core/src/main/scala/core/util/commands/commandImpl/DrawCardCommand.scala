package core
package util.commands.commandImpl

import model.Move
import model.fieldComponent.FieldInterface
import core.util.commands.CommandInterface

import scala.util.{Failure, Try, Success}

class DrawCardCommand(val field: FieldInterface)
    extends CommandInterface() {
  var memento: FieldInterface = field
  override def doStep: Try[FieldInterface] =
    Option
      .when(checkConditions)({
        memento = field
        field.drawCard()
      })
      .toRight(new Exception("Your hand is full!"))
      .toTry

  def checkConditions: Boolean =
    field.players(field.activePlayerId).hand.length < 5
}
