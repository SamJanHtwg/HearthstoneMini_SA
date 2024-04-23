package core.util.commands

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.cardComponent.cardImpl.Card
import model.playerComponent.playerImpl.Player
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import core.util.commands.commandImpl.SwitchPlayerCommand
import model.playerComponent.PlayerInterface

class SwitchPlayerCommandSpec extends AnyWordSpec with Matchers {
  "A controller" should {
    "when switching players" in {
      val testField = Field(
        players = Map[Int, PlayerInterface](
          (
            1,
            Player(
              name = "test",
              id = 1
            )
          ),
          (
            2,
            Player(
              name = "test",
              id = 2
            )
          )
        )
      )
      val switchPlayer = new SwitchPlayerCommand(testField)
      val result = switchPlayer.doStep
      result.get.activePlayerId should be(2)
      switchPlayer.memento should be(testField)

    }
  }
}
