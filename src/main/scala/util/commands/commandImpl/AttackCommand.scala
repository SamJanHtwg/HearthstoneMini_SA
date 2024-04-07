package hearthstoneMini
package util.commands.commandImpl

import model.Move
import controller.GameState
import controller.component.controllerImpl.Controller
import model.fieldComponent.FieldInterface
import scala.util.{Failure, Success, Try}
import hearthstoneMini.util.commands.CommandInterface
import org.checkerframework.checker.units.qual.s

//noinspection DuplicatedCode
class AttackCommand(controller: Controller, move: Move)
    extends CommandInterface {
  var memento: FieldInterface = controller.field
  var newField: FieldInterface = _

  override def doStep: Try[FieldInterface] = {
    val currentField = controller.field

    val activeFieldSlot = currentField
      .players(currentField.activePlayerId)
      .field(move.fieldSlotActive)

    val inactiveFieldSlot = currentField
      .players(currentField.getInactivePlayerId)
      .field(move.fieldSlotInactive)

    activeFieldSlot match {
      case None =>
        return Failure(
          Exception("Stelle sicher, dass du eine Karte ausgewählt hast!")
        )
      case Some(attackingCard) =>
        inactiveFieldSlot match {
          case None =>
            return Failure(Exception("Du kannst kein leeres Feld angreifen!"))
          case Some(defendingCard) =>
            attackingCard.attackCount > 0 match {
              case false =>
                return Failure(
                  Exception("Jede Karte kann nur einmal pro Zug angreifen!")
                )
              case true =>
                controller.field.turns > 1 match {
                  case false =>
                    return Failure(
                      Exception(
                        "Kein Spieler kann in seineim ersten Zug angreifen!"
                      )
                    )
                  case true => {
                    val difference =
                      attackingCard.attValue
                        - defendingCard.defenseValue

                    if (difference < 0) {
                      newField = currentField
                        .destroyCard(
                          currentField.getInactivePlayerId,
                          move.fieldSlotInactive
                        )
                        .reduceHp(
                          currentField.getInactivePlayerId,
                          Math.abs(difference)
                        )
                    } else {
                      newField = currentField
                        .reduceDefVal(
                          move.fieldSlotInactive,
                          attackingCard.attValue
                        )
                        .reduceAttackCount(move.fieldSlotActive)
                    }

                    if newField.players.values.filter(_.isHpEmpty).size != 0
                    then controller.nextState()
                    Success(newField)
                  }
                }
            }
        }
    }
  }

  override def checkConditions: Boolean = ???

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
