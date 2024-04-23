package core
package util.commands.commandImpl

import model.Move
import model.GameState
import core.util.commands.CommandInterface
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import scala.util.{Success, Try, Failure}
import model.cardComponent.CardInterface

//noinspection DuplicatedCode
class DirectAttackCommand(val field: FieldInterface, move: Move)
    extends CommandInterface {
  var memento: FieldInterface = field
  var errorMsg: String = ""
  override def doStep: Try[FieldInterface] = {
    checkConditions((attackingCard: CardInterface) => {
      memento = field
      val currentField = field

      var newField = currentField
        .reduceHp(
          currentField.getInactivePlayerId,
          attackingCard.attValue
        )
        .reduceAttackCount(move.fieldSlotActive)

      if (newField.players.values.filter(_.isHpEmpty).size != 0) {
        newField = newField.setGameState(GameState.WIN)
      }
      newField
    })
  }

  def checkConditions(
      onSuccess: (
          attackingCard: CardInterface
      ) => FieldInterface
  ): Try[FieldInterface] = {
    val currentField = field

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
            if field.turns > 1 then Right(onSuccess(attackingCard))
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
