package hearthstoneMini
package util.commands.commandImpl

import controller.component.controllerImpl.Controller
import model.Move
import model.fieldComponent.FieldInterface
import hearthstoneMini.util.commands.CommandInterface

import scala.util.{Failure, Try, Success}

class DrawCardCommand(controller: Controller) extends CommandInterface {
  var memento: FieldInterface = controller.field
  override def doStep: Try[FieldInterface] =
    Option
      .when(checkConditions)({
        memento = controller.field
        controller.field.drawCard()
      })
      .toRight(new Exception("Your hand is full!"))
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

  def checkConditions: Boolean =
    controller.field.players(controller.field.activePlayerId).hand.length < 5
}
