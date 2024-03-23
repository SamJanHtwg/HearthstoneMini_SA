package hearthstoneMini
package model.commands

import model.Move
import controller.GameState
import controller.component.controllerImpl.Controller
import model.fieldComponent.FieldInterface
import util.Command
import scala.util.{Failure, Success, Try}

class AttackCommand(controller: Controller, move: Move) extends Command {
  var memento: FieldInterface = controller.field
  var newField: FieldInterface = null
  var errorMsg: String = ""

  override def doStep: Try[FieldInterface] = {
    if checkConditions then {
      val difference = Math.abs(controller.field.players.head.fieldbar.cardArea.slot(move.fieldSlotActive).get.attValue
        - controller.field.players(1).fieldbar.cardArea.slot(move.fieldSlotInactive).get.defenseValue)
      newField = controller.field.reduceDefVal(move.fieldSlotInactive, 
        controller.field.players.head.fieldbar.cardArea.slot(move.fieldSlotActive).get.attValue)
      newField = newField.reduceAttackCount(move.fieldSlotActive)
      if newField.players(1).fieldbar.cardArea.slot(move.fieldSlotInactive).get.defenseValue <= 0 then
        newField = newField.destroyCard(1, move.fieldSlotInactive).reduceHp(1, difference)

      if (newField.players(0).gamebar.hp.isEmpty || newField.players(1).gamebar.hp.isEmpty)
      then controller.nextState()
      Success(newField)
    }
    else Failure(Exception(errorMsg))
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
      if controller.field.players(1).fieldbar.cardArea.slot(move.fieldSlotInactive).isDefined then
        if controller.field.players.head.fieldbar.cardArea.slot(move.fieldSlotActive).get.attackCount >= 1 then
          if controller.field.turns > 1 then return true
          else
            errorMsg = "No player can attack in his first turn!"
        else
          errorMsg = "Each Card can only attack once each turn!"
      else
        errorMsg = "Make sure you select a Card of your Opponent"
    else
      errorMsg = "You cant attack with an empty Card slot!"
    false
}