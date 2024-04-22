import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.cardComponent.cardImpl.Card
import model.fieldComponent.fieldImpl.Field
import model.playerComponent.playerImpl.Player
import model.Move
import core.controller.component.controllerImpl.Controller
import core.util.commands.commandImpl.AttackCommand
import model.playerComponent.PlayerInterface
import model.GameState

class AttackCommandSpec extends AnyWordSpec with Matchers {
  val testCards: List[Card] = List[Card](
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 10, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 20, "testEffect1", "testRarety1", 1, "")
  )

  "A AttackCommand" should {
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
      val attackCommand = new AttackCommand(testField, Move(0, 1))
      attackCommand.doStep
      attackCommand.memento should be(testField)
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
      val attackCommand = new AttackCommand(testField, Move(0, 1))

      attackCommand.undoStep(testField)
      attackCommand.memento should be(testField)
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
      val attackCommand = new AttackCommand(testField, Move(0, 1))

      attackCommand.redoStep(testField)
      attackCommand.memento should be(testField)
    }
    "reduce enemies hp" in {
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
              field = Vector.tabulate(5) { field =>
                Some(testCards(0).copy(defenseValue = 10))
              }
            )
          )
        )
      )
      val attackCommand = new AttackCommand(testField, Move(0, 0))

      val result = attackCommand.doStep
      result.get.players(2).field(0).get.defenseValue should be(9)
      attackCommand.memento should be(testField)
    }
    "fail when card has already attacked this turn" in {
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
              deck = testCards,
              field = Vector.tabulate(5) { field => Some(testCards(0)) }
            )
          )
        )
      )
      val attackCommand = new AttackCommand(testField, Move(0, 0))

      val result = attackCommand.doStep
      result.isFailure should be(true)
      attackCommand.memento should be(testField)
    }
    "fail when its the first turn" in {
      val testField = Field(
        turns = 1,
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
      val attackCommand = new AttackCommand(testField, Move(0, 0))

      val result = attackCommand.doStep
      result.isFailure should be(true)
      attackCommand.memento should be(testField)
    }
    "fail when enemies field is empty" in {
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
      val attackCommand = new AttackCommand(testField, Move(0, 0))

      val result = attackCommand.doStep
      result.isFailure should be(true)
      attackCommand.memento should be(testField)
    }
    "set win state when criteria is met" in {
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
              hpValue = 0,
              name = "test",
              id = 2,
              hand = testCards,
              deck = testCards,
              field = Vector.tabulate(5) { field => Some(testCards(0)) }
            )
          )
        )
      )
      val attackCommand = new AttackCommand(testField, Move(0, 0))

      val result = attackCommand.doStep
      result.get.gameState should be(GameState.WIN)
      attackCommand.memento should be(testField)
    }
  }
}
