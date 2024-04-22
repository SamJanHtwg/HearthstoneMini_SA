package model.commands

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.playerComponent.playerImpl
import model.fieldComponent.fieldImpl.Field
import model.playerComponent.playerImpl.Player
import model.Move
import core.util.commands.commandImpl.DirectAttackCommand
import model.cardComponent.cardImpl.Card
import model.playerComponent.PlayerInterface

class DirectAttackCommandSpec extends AnyWordSpec with Matchers {
  val testCards: List[Card] = List[Card](
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, "")
  )

  "A DirectAttackCommand" should {
    "do step" in {
      val testField = Field(
        turns = 4,
        players = Map[Int, PlayerInterface](
          (
            1,
            Player(
              name = "test",
              id = 1,
              hand = testCards,
              deck = testCards,
              field = Vector.tabulate(5) { field => Some(testCards(0)) }
            )
          ),
          (
            2,
            Player(
              name = "test",
              id = 2,
              hand = testCards,
              deck = testCards
            )
          )
        )
      )
      val directAttackCommand = new DirectAttackCommand(
        testField,
        Move(fieldSlotActive = 1)
      )
      val result = directAttackCommand.doStep
      result.isSuccess should be(true)
    }
    "fail on do step when in first turn" in {
      val testField = Field(
        players = Map[Int, PlayerInterface](
          (
            1,
            Player(
              name = "test",
              id = 1,
              hand = testCards,
              deck = testCards,
              field = Vector.tabulate(5) { field => Some(testCards(0)) }
            )
          ),
          (
            2,
            Player(
              name = "test",
              id = 2,
              hand = testCards,
              deck = testCards
            )
          )
        )
      )
      val directAttackCommand = new DirectAttackCommand(
        testField,
        Move(fieldSlotActive = 1)
      )
      val result = directAttackCommand.doStep
      result.isFailure should be(true)
    }
    "fail on do step when no card on the field" in {
      val testField = Field(
        turns = 4,
        players = Map[Int, PlayerInterface](
          (
            1,
            Player(
              name = "test",
              id = 1,
              hand = testCards,
              deck = testCards
            )
          ),
          (
            2,
            Player(
              name = "test",
              id = 2,
              hand = testCards,
              deck = testCards
            )
          )
        )
      )
      val directAttackCommand = new DirectAttackCommand(
        testField,
        Move(fieldSlotActive = 1)
      )
      val result = directAttackCommand.doStep
      result.isFailure should be(true)
    }
    "fail on do step when card has already attacked" in {
      val testField = Field(
        turns = 4,
        players = Map[Int, PlayerInterface](
          (
            1,
            Player(
              name = "test",
              id = 1,
              hand = testCards,
              deck = testCards,
              field = Vector.tabulate(5) { field =>
                Some(testCards(0).copy(attackCount = 0))
              }
            )
          ),
          (
            2,
            Player(
              name = "test",
              id = 2,
              hand = testCards,
              deck = testCards
            )
          )
        )
      )
      val directAttackCommand = new DirectAttackCommand(
        testField,
        Move(fieldSlotActive = 1)
      )
      val result = directAttackCommand.doStep
      result.isFailure should be(true)
    }
    "fail on do step when the enemy has a card on the field" in {
      val testField = Field(
        turns = 4,
        players = Map[Int, PlayerInterface](
          (
            1,
            Player(
              name = "test",
              id = 1,
              hand = testCards,
              deck = testCards,
              field = Vector.tabulate(5) { field => Some(testCards(0)) }
            )
          ),
          (
            2,
            Player(
              name = "test",
              id = 2,
              hand = testCards,
              deck = testCards,
              field = Vector.tabulate(5) { field => Some(testCards(0)) }
            )
          )
        )
      )
      val directAttackCommand = new DirectAttackCommand(
        testField,
        Move(fieldSlotActive = 1)
      )
      val result = directAttackCommand.doStep
      result.isFailure should be(true)
    }
    "undo step" in {
      val testField = Field(
        players = Map[Int, PlayerInterface](
          (
            1,
            Player(
              name = "test",
              id = 1,
              hand = testCards,
              deck = testCards
            )
          ),
          (
            2,
            Player(
              name = "test",
              id = 2,
              hand = testCards,
              deck = testCards
            )
          )
        )
      )
      val directAttackCommand = new DirectAttackCommand(
        testField,
        Move(fieldSlotActive = 1)
      )
      directAttackCommand.undoStep(testField)
      directAttackCommand.memento should be(testField)
    }
    "redo step" in {
      val testField = Field(
        players = Map[Int, PlayerInterface](
          (
            1,
            Player(
              name = "test",
              id = 1,
              hand = testCards,
              deck = testCards
            )
          ),
          (
            2,
            Player(
              name = "test",
              id = 2,
              hand = testCards,
              deck = testCards
            )
          )
        )
      )
      val directAttackCommand = new DirectAttackCommand(
        testField,
        Move(fieldSlotActive = 1)
      )
      directAttackCommand.redoStep(testField)
      directAttackCommand.memento should be(testField)
    }
  }
}
