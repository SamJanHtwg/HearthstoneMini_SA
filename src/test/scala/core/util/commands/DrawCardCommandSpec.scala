package core.util.commands

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.cardComponent.cardImpl.Card
import model.playerComponent.playerImpl.Player
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import model.playerComponent.PlayerInterface
import core.util.commands.commandImpl.DrawCardCommand

class DrawCardCommandSpec extends AnyWordSpec with Matchers {
  val testCards: List[Card] = List[Card](
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, "")
  )
  "A DrawCardCommand" should {
    "add a card to the hand" in {
      val testField = Field(
        players = Map[Int, PlayerInterface](
          (
            1,
            Player(
              name = "test",
              id = 1,
              deck = testCards
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
      val drawCardCommand = new DrawCardCommand(testField)
      val result = drawCardCommand.doStep
      result.get.players(1).hand.length should be(1)
      drawCardCommand.memento should be(testField)
    }

    "fail when hand is full" in {
      val testField = Field(
        players = Map[Int, PlayerInterface](
          (
            1,
            Player(
              name = "test",
              id = 1,
              deck = testCards,
              hand = testCards
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
      val drawCardCommand = new DrawCardCommand(testField)
      val result = drawCardCommand.doStep
      result.isFailure should be(true)
      drawCardCommand.memento should be(testField)
    }
  }
}
