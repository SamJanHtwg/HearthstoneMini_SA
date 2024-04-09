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
import hearthstoneMini.model.cardComponent.CardInterface

//noinspection DuplicatedCode
class DirectAttackCommand(controller: Controller, move: Move)
    extends CommandInterface {
  var memento: FieldInterface = controller.field
  var errorMsg: String = ""
  override def doStep: Try[FieldInterface] = {
    checkConditions((attackingCard: CardInterface) => {
      memento = controller.field
      val currentField = controller.field

      val newField = currentField
        .reduceHp(
          currentField.getInactivePlayerId,
          attackingCard.attValue
        )
        .reduceAttackCount(move.fieldSlotActive)

      if (newField.players.values.filter(_.isHpEmpty).size != 0) {
        controller.nextState()
      }
      newField
    })
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

  def checkConditions(
      onSuccess: (
          attackingCard: CardInterface
      ) => FieldInterface
  ): Try[FieldInterface] = {
    val currentField = controller.field

    val activeFieldSlot = currentField
      .players(currentField.activePlayerId)
      .field(move.fieldSlotActive)

    val isEnemyFieldEmpty = !currentField
      .players(currentField.getInactivePlayerId)
      .field
      .exists(_.isDefined)

    activeFieldSlot
      .toRight(Exception("Stelle sicher, dass du eine Karte ausgewählt hast!"))
      .flatMap(attackingCard =>
        if isEnemyFieldEmpty then
          if attackingCard.attackCount >= 1 then
            if controller.field.turns > 1 then Right(onSuccess(attackingCard))
            else
              Left(
                Exception("Kein Spieler kann in seiner ersten Runde angreifen!")
              )
          else
            Left(Exception("Jede Karte kann nur einmal pro Runde angreifen!"))
        else
          Left(
            Exception(
              "Stellen Sie sicher, dass das Feld Ihres Gegners leer ist, bevor Sie direkt angreifen"
            )
          )
      )
      .toTry
  }
}
