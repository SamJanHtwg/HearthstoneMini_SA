package hearthstoneMini
package util.commands.commandImpl

import model.Move
import controller.GameState
import controller.component.controllerImpl.Controller
import model.fieldComponent.FieldInterface
import scala.util.{Failure, Success, Try}
import hearthstoneMini.util.commands.CommandInterface

//noinspection DuplicatedCode
class AttackCommand(controller: Controller, move: Move) extends CommandInterface {
  var memento: FieldInterface = controller.field
  var newField: FieldInterface = _
  var errorMsg: String = ""

  override def doStep: Try[FieldInterface] = {
    if checkConditions then {
      val difference = Math.abs(
        controller.field
          .players(controller.field.activePlayerId)
          .field(move.fieldSlotActive)
          .get
          .attValue
          - controller.field
            .players(controller.field.getInactivePlayerId)
            .field(move.fieldSlotInactive)
            .get
            .defenseValue
      )
      newField = controller.field.reduceDefVal(
        move.fieldSlotInactive,
        controller.field
          .players(controller.field.activePlayerId)
          .field(move.fieldSlotActive)
          .get
          .attValue
      )
      newField = newField.reduceAttackCount(move.fieldSlotActive)
      if newField
          .players(controller.field.getInactivePlayerId)
          .field(move.fieldSlotInactive)
          .get
          .defenseValue <= 0
      then
        newField = newField
          .destroyCard(
            controller.field.getInactivePlayerId,
            move.fieldSlotInactive
          )
          .reduceHp(controller.field.getInactivePlayerId, difference)

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
      if controller.field
          .players(controller.field.getInactivePlayerId)
          .field(move.fieldSlotInactive)
          .isDefined
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
      else errorMsg = "Make sure you select a Card of your Opponent"
    else errorMsg = "You cant attack with an empty Card slot!"
    false
}
