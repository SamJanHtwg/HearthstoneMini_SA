import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import core.model.cardComponent.cardImpl.Card
import core.model.fieldComponent.fieldImpl.Field
import core.model.playerComponent.playerImpl.Player
import core.model.Move
import core.controller.component.controllerImpl.Controller
import core.util.commands.commandImpl.AttackCommand

class AttackCommandSpec extends AnyWordSpec with Matchers {
  val testCards: List[Card] = List[Card](
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 10, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 20, "testEffect1", "testRarety1", 1, "")
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
    controller.placeCard(Move(1, 1))
    controller.switchPlayer()
    controller.placeCard(Move(0, 1))
    controller.switchPlayer()
    val attackCommand = new AttackCommand(
      controller,
      Move(fieldSlotActive = 1, fieldSlotInactive = 1)
    )

    "do step" in {
      val field = controller.field
      attackCommand.doStep
      attackCommand.memento should be(field)
    }
    "undo step" in {
      val field = controller.field
      attackCommand.undoStep
      attackCommand.memento should be(field)
    }
    "redo step" in {
      val field = controller.field
      attackCommand.redoStep
      attackCommand.memento should be(field)
    }
  }
}
