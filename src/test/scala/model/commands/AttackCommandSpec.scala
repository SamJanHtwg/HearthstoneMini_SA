import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.cardComponent.cardImpl.Card
import model.fieldComponent.fieldImpl.Field
import model.playerComponent.playerImpl.Player
import model.Move
import core.controller.component.controllerImpl.Controller
import core.util.commands.commandImpl.AttackCommand
import org.scalamock.scalatest.MockFactory

class AttackCommandSpec extends AnyWordSpec with Matchers with MockFactory {
  val testCards: List[Card] = List[Card](
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 10, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 20, "testEffect1", "testRarety1", 1, "")
  )

  "A controller" should {
    val controller = mock[Controller]

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
