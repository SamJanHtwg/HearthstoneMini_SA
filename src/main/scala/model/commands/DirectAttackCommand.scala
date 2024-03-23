package hearthstoneMini
package model.commands

import model.Move
import controller.GameState
import controller.component.controllerImpl.Controller
import model.cardComponent.cardImpl.Card
import util.Command
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import scala.util.{Success, Try, Failure}

class DirectAttackCommand(controller: Controller, move: Move) extends Command {
  var memento: FieldInterface = controller.field
  var errorMsg: String = ""
  override def doStep: Try[FieldInterface] = {
    if checkConditions then {
      memento = controller.field
      val newField = controller.field.reduceHp(1, controller.field.players.head.fieldbar.cardArea.
        slot(move.fieldSlotActive).get.attValue).reduceAttackCount(move.fieldSlotActive)
      if (newField.players(0).gamebar.hp.isEmpty || newField.players(1).gamebar.hp.isEmpty)
      then controller.nextState()
      Success(newField)
    } else
      Failure(Exception(errorMsg))
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

  override def checkConditions: Boolean =
    if controller.field.players.head.fieldbar.cardArea.slot(move.fieldSlotActive).isDefined then
      if !(controller.field.players(1).fieldbar.cardArea.row.count(_.isDefined) > 0) then
        if controller.field.players.head.fieldbar.cardArea.slot(move.fieldSlotActive).get.attackCount >= 1 then
          if controller.field.turns > 1 then return true
          else
            errorMsg = "No player can attack in his first turn!"
        else
          errorMsg = "Each Card can only attack once each turn!"
      else
        errorMsg = "Make sure your Opponents field is empty before you attack directly"
    else
      errorMsg = "You cant attack with an empty Card slot!"
    false
}