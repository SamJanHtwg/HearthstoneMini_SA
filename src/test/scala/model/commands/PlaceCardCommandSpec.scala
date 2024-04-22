package hearthstoneMini.model.commands

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import _root_.model.fieldComponent.FieldInterface
import _root_.model.Move
import core.util.commands.commandImpl.PlaceCardCommand
import model.fieldComponent.fieldImpl.Field
import org.scalamock.scalatest.MockFactory
import persistence.fileIO.FileIOInterface
import scalafx.scene.input.KeyCode.F
import model.playerComponent.playerImpl.Player
import model.cardComponent.cardImpl.Card
import model.playerComponent.PlayerInterface
import scala.annotation.meta.field
import scala.util.Try
import scala.util.Failure

//TODO: When undo redo command are working, add tests for them

class PlaceCardCommandSpec extends AnyWordSpec with Matchers {
  "A PlaceCardCommand" should {
    val testCards = List(
      Card("testcard1", 1, 1, 1, "effect", "rarety", 1, "id1"),
      Card("testcard2", 1, 1, 1, "effect", "rarety", 1, "id2"),
      Card("testcard3", 1, 1, 1, "effect", "rarety", 1, "id3"),
      Card("testcard4", 1, 1, 1, "effect", "rarety", 1, "id4"),
      Card("testcard5", 1, 1, 1, "effect", "rarety", 1, "id5")
    )
    "do step" in {

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

      val placeCardCommand = new PlaceCardCommand(
        testField,
        Move(handSlot = 2, fieldSlotActive = 2)
      )

      placeCardCommand.doStep
      placeCardCommand.memento should be(testField)
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
        ))

      val placeCardCommand = new PlaceCardCommand(
        testField,
        Move(handSlot = 2, fieldSlotActive = 2)
      )

      placeCardCommand.undoStep(testField)
      placeCardCommand.memento should be(testField)
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
        ))

    
      val placeCardCommand = new PlaceCardCommand(
        testField,
        Move(handSlot = 2, fieldSlotActive = 2)
      )
      placeCardCommand.redoStep(testField)
      placeCardCommand.memento should be(testField)
    }
    "return a failure when criteria is not met" in {
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
              id = 2,
              deck = testCards
            )
          )
        ))

    
      val placeCardCommand = new PlaceCardCommand(
        testField,
        Move(handSlot = 2, fieldSlotActive = 2)
      )
      val result = placeCardCommand.doStep
      result.isFailure should be(true)
      placeCardCommand.memento should be(testField)
    }
  }
}
