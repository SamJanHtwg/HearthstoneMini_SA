package hearthstoneMini
package util.commands.commandImpl

import model.Move
import controller.GameState
import controller.component.controllerImpl.Controller
import model.fieldComponent.FieldInterface
import scala.util.{Failure, Success, Try}
import hearthstoneMini.util.commands.CommandInterface
import org.checkerframework.checker.units.qual.s
import hearthstoneMini.model.cardComponent.CardInterface

//noinspection DuplicatedCode
class AttackCommand(controller: Controller, move: Move)
    extends CommandInterface {
  var memento: FieldInterface = controller.field
  var newField: FieldInterface = _

  override def doStep: Try[FieldInterface] = checkConditions(
    (attackingCard: CardInterface, defendingCard: CardInterface) => {
      val currentField = controller.field
      val difference =
        attackingCard.attValue
          - defendingCard.defenseValue

      newField = {
        if (difference > 0) {
          currentField
            .destroyCard(
              currentField.getInactivePlayerId,
              move.fieldSlotInactive
            )
            .reduceHp(
              currentField.getInactivePlayerId,
              Math.abs(difference)
            )
        } else {
          currentField
            .reduceDefVal(
              move.fieldSlotInactive,
              attackingCard.attValue
            )
        }
      }.reduceAttackCount(move.fieldSlotActive)

      if (newField.players.values.filter(_.isHpEmpty).size != 0) {
        controller.nextState()
      }
      newField
    }
  )

  def checkConditions(
      onSuccess: (
          attackingCard: CardInterface,
          defendingCard: CardInterface
      ) => FieldInterface
  ): Try[FieldInterface] = {
    val currentField = controller.field

    val activeFieldSlot = currentField
      .players(currentField.activePlayerId)
      .field(move.fieldSlotActive)

    val inactiveFieldSlot = currentField
      .players(currentField.getInactivePlayerId)
      .field(move.fieldSlotInactive)

    activeFieldSlot
      .toRight(Exception("Stelle sicher, dass du eine Karte ausgewählt hast!"))
      .flatMap(attackingCard =>
        inactiveFieldSlot
          .toRight(Exception("Du kannst kein leeres Feld angreifen!"))
          .flatMap(defendingCard =>
            attackingCard.attackCount > 0 match {
              case false =>
                Left(
                  Exception("Jede Karte kann nur einmal pro Zug angreifen!")
                )
              case true =>
                currentField.turns > 1 match {
                  case false =>
                    Left(
                      Exception(
                        "Kein Spieler kann in seineim ersten Zug angreifen!"
                      )
                    )
                  case true => Right(onSuccess(attackingCard, defendingCard))
                }
            }
          )
      )
      .toTry
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
