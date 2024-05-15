package core.util

import core.util.commands.CommandInterface
import model.fieldComponent.FieldInterface
import core.util.UndoManager
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.cardComponent.cardImpl.Card
import model.cardComponent.CardInterface
import model.playerComponent.playerImpl.Player
import model.fieldComponent.fieldImpl.Field
import scala.util.{Success, Try}
import scala.util.Failure

class UndoManagerSpec extends AnyWordSpec with Matchers {
  val testCards: List[Card] = List(
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test2", 2, 2, 2, "testEffect2", "testRarety2", 0, "")
  )

  val graveyard: Array[CardInterface] = Array(
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, "")
  )

  val testCardsPlayer1: Player =
    Player("Player1", 1, deck = testCards, friedhof = graveyard)
  val testCardsPlayer2: Player =
    Player("Player2", 2, deck = testCards, friedhof = graveyard)

  val testField: Field = Field(players =
    Map[Int, Player](
      (1, testCardsPlayer1),
      (2, testCardsPlayer2)
    )
  )

  val command = new CommandInterface {
    var memento: FieldInterface = testField
    val field: FieldInterface = testField

    override def doStep: Try[FieldInterface] = Success(testField)
    override def undoStep(currentField: FieldInterface): FieldInterface =
      testField
    override def redoStep(currentField: FieldInterface): FieldInterface =
      testField
  }

  "Undo Manager" should {
    "not be able to do undo/redo at beginning" in {
      val undoManager = new UndoManager()
      undoManager.canUndo() should be(false)
      undoManager.canRedo() should be(false)
    }

    "and throw exceptions" in {
      val undoManager = new UndoManager()
      val resultUndo = undoManager.undoStep(testField)
      val resultRedo = undoManager.redoStep(testField)

      resultUndo shouldBe a[Failure[_]]
      resultUndo.failed.get.getMessage should be("No more undo steps available")

      resultRedo shouldBe a[Failure[_]]
      resultRedo.failed.get.getMessage should be("No more redo steps available")
    }

    "be able to save step" in {
      val undoManager = new UndoManager()
      undoManager.doStep(command)
      undoManager.undoStack.size should be(1)
    }

    "be able to undo step" in {
      val undoManager = new UndoManager()
      undoManager.doStep(command)
      undoManager.undoStep(testField) should be(Success(testField))
      undoManager.undoStack.size should be(0)
      undoManager.redoStack.size should be(1)
    }

    "be able to redo step" in {
      val undoManager = new UndoManager()
      undoManager.doStep(command)
      undoManager.undoStep(testField)
      undoManager.redoStep(testField) should be(Success(testField))
      undoManager.redoStack.size should be(0)
      undoManager.undoStack.size should be(1)
    }
  }

}
