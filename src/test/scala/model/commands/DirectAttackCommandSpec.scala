import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import model.playerComponent.playerImpl
import core.controller.component.controllerImpl.Controller
import model.fieldComponent.fieldImpl.Field
import model.playerComponent.playerImpl.Player
import model.Move
import core.util.commands.commandImpl.DirectAttackCommand
import model.cardComponent.cardImpl.Card

class DirectAttackCommandSpec extends AnyWordSpec with Matchers {
  val testCards: List[Card] = List[Card](
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, "")
  )

  "A controller" should {
    val controller = Controller(
      Field(
        players = Map[Int, Player](
          (1, Player(id = 1, hand = testCards)),
          (2, Player(id = 2, hand = testCards))
        )
      )
    )

    controller.placeCard(Move())
    controller.switchPlayer()
    controller.switchPlayer()
    val field = controller.field
    val directAttackCommand = new DirectAttackCommand(controller, Move())
    "do step" in {
      directAttackCommand.doStep
      directAttackCommand.memento should be(field)
    }
    "undo step" in {
      val field = controller.field
      directAttackCommand.undoStep
      directAttackCommand.memento should be(field)
    }
    "redo step" in {
      directAttackCommand.redoStep
      directAttackCommand.memento should be(field)
    }
  }
}
