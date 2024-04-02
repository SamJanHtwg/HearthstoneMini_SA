package hearthstoneMini
package util.commands.commandImpl

import model.Move
import controller.GameState
import controller.component.controllerImpl.Controller
import model.cardComponent.cardImpl.Card
import hearthstoneMini.util.commands.CommandInterface
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import scala.util.{Success, Try, Failure}

//noinspection DuplicatedCode
class DirectAttackCommand(controller: Controller, move: Move) extends CommandInterface {
  var memento: FieldInterface = controller.field
  var errorMsg: String = ""
  override def doStep: Try[FieldInterface] = {
    if checkConditions then {
      memento = controller.field
      val newField = controller.field
        .reduceHp(
          controller.field.getInactivePlayerId,
          controller.field
            .players(controller.field.activePlayerId)
            .field(move.fieldSlotActive)
            .get
            .attValue
        )
        .reduceAttackCount(move.fieldSlotActive)
      if newField.players.values.filter(_.isHpEmpty).size != 0
      then controller.nextState()
      Success(newField)
    } else Failure(Exception(errorMsg))
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
    if controller.field
        .players(controller.field.activePlayerId)
        .field(move.fieldSlotActive)
        .isDefined
    then
      if !(controller.field
          .players(controller.field.getInactivePlayerId)
          .field
          .count(_.isDefined) > 0)
      then
        if controller.field
            .players(controller.field.activePlayerId)
            .field(move.fieldSlotActive)
            .get
            .attackCount >= 1
        then
          if controller.field.turns > 1 then return true
          else errorMsg = "No player can attack in his first turn!"
        else errorMsg = "Each Card can only attack once each turn!"
      else
        errorMsg =
          "Make sure your Opponents field is empty before you attack directly"
    else errorMsg = "You cant attack with an empty Card slot!"
    false
}
