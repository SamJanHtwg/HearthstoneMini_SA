import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.cardComponent.cardImpl.Card
import model.fieldComponent.fieldImpl.Field
import model.playerComponent.playerImpl.Player
import model.Move
import core.controller.component.controllerImpl.Controller
import core.util.commands.commandImpl.AttackCommand

class AttackCommandSpec extends AnyWordSpec with Matchers {
  val testCards: List[Card] = List[Card](
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 10, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 20, "testEffect1", "testRarety1", 1, "")
  )

  // "A AttackCommand" should {
  //   "do step" in {
  //     val field = controller.field
  //     val attackCommand = new AttackCommand(controller, Move(0, 1))
  //     attackCommand.doStep
  //     attackCommand.memento should be(field)
  //   }
  //   "undo step" in {
  //     val field = controller.field
  //     attackCommand.undoStep
  //     attackCommand.memento should be(field)
  //   }
  //   "redo step" in {
  //     val field = controller.field
  //     attackCommand.redoStep
  //     attackCommand.memento should be(field)
  //   }
  // }
}
