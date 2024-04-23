package hearthstoneMini.controller

import core.controller.{Strategy}
import _root_.model.GameState.GameState
import _root_.model.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import core.controller.component.controllerImpl.Controller
import _root_.model.cardComponent.cardImpl.Card
import _root_.model.fieldComponent.fieldImpl.Field
import _root_.model.playerComponent.playerImpl.Player
import _root_.model.Move
import org.scalamock.scalatest.MockFactory
import persistence.fileIO.FileIOInterface
import core.util.CardProvider
import core.util.UndoManager
import scala.util.Failure
import core.util.Observer
import org.scalatest.BeforeAndAfterEach
import core.util.Event
import scala.util.Success
import scala.annotation.meta.field
import model.fieldComponent.FieldInterface

class ControllerSpec
    extends AnyWordSpec
    with Matchers
    with MockFactory
    with BeforeAndAfterEach {
  var testCards: List[Card] = _
  var mockUndoManager: UndoManager = _
  var mockCardProvider: CardProvider = _
  var mockFileIO: FileIOInterface = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockCardProvider = CardProvider(inputFile = "/json/cards.json")
    mockUndoManager = mock[UndoManager]
    mockFileIO = mock[FileIOInterface]

    testCards = List[Card](
      Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
      Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
      Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
      Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, "")
    )
  }

  "The Controller" should {
    "should have access to all game states" in {
      val allStates = GameState.values.toList
      assert(allStates.length == 5)
    }
    "have a default game state of GameState.PREGAME" in {
      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1).resetAndIncreaseMana()),
          (2, Player(id = 2))
        )
      )
      controller.field.gameState should be(GameState.CHOOSEMODE)
    }
    "place a card on field" in {
      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)

      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1, manaValue = 100, hand = testCards)),
          (2, Player(id = 2))
        ),
        turns = 3
      )
      (mockUndoManager.doStep _).expects(*).once()
      controller.placeCard(Move(2, 2))
      controller.field
        .players(controller.field.activePlayerId)
        .field(2)
        .isDefined should be(true)
    }
    "draw a card" in {
      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)
      controller.field = Field(
        players = Map[Int, Player](
          (
            1,
            Player(id = 1, hand = testCards.take(4), deck = testCards)
              .resetAndIncreaseMana()
          ),
          (2, Player(id = 2))
        )
      )
      (mockUndoManager.doStep _).expects(*).once()
      controller.drawCard()
      controller.field
        .players(controller.field.activePlayerId)
        .hand
        .length should be(5)
    }
    "setting player names" in {

      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1).resetAndIncreaseMana()),
          (2, Player(id = 2))
        )
      )
      controller.setPlayerNames(playername1 = "Jan", playername2 = "Sam")
      controller.field.players(controller.field.activePlayerId).name should be(
        "Jan"
      )
      controller.field.players(2).name should be("Sam")
    }
    "attacking" in {
      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)
      controller.field = Field(
        turns = 3,
        players = Map[Int, Player](
          (
            1,
            Player(
              id = 1,
              hand = testCards,
              field = Vector.tabulate(5) { field => Some(testCards(0)) }
            ).resetAndIncreaseMana()
          ),
          (
            2,
            Player(
              id = 2,
              hand = testCards,
              field = Vector.tabulate(5) { field => Some(testCards(0)) }
            )
          )
        )
      )

      (mockUndoManager.doStep _).expects(*).once()

      controller.attack(Move(fieldSlotActive = 2, fieldSlotInactive = 2))
    }
    "switching player" in {

      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1, name = 1.toString).resetAndIncreaseMana()),
          (2, Player(id = 2, name = 2.toString))
        )
      )
      (mockUndoManager.doStep _).expects(*).once()
      controller.switchPlayer()
      controller.field.players(controller.field.activePlayerId).name should be(
        "2"
      )
    }
    "do a direct attack" in {
      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1, manaValue = 100, hand = testCards)),
          (2, Player(id = 2, hpValue = 5))
        ),
        turns = 3
      )
      (mockUndoManager.doStep _).expects(*).twice()
      controller.placeCard(Move(2, 2))
      controller.directAttack(Move(fieldSlotActive = 2))
      controller.field.players(2).hpValue should be(4)
    }
    "undo step / redo step" in {
      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1, hand = List.empty, deck = testCards)),
          (2, Player(id = 2))
        )
      )
      (mockUndoManager.undoStep _).expects(*).returns(Success(controller.field))
      (mockUndoManager.redoStep _).expects(*).returns(Success(controller.field))

      controller.undo
      controller.redo
    }
    "setStrategy should set a strategy based on input" in {

      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1).resetAndIncreaseMana()),
          (2, Player(id = 2))
        )
      )
      controller.setStrategy(Strategy.debug)
      controller.field.getPlayerById(1).hpValue should be(100)
      controller.field.getPlayerById(1).manaValue should be(100)
    }
    "should set game state to Exit" in {

      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1).resetAndIncreaseMana()),
          (2, Player(id = 2))
        )
      )
      controller.exitGame()
      controller.field.gameState should be(GameState.EXIT)
    }
    "should return the Winner when one player has 0 hp" in {

      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)

      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1, hpValue = 0)),
          (2, Player(id = 2))
        ),
        turns = 2
      )

      controller.getWinner() should be(
        Some(controller.field.players(controller.field.activePlayerId).name)
      )
    }
    "should return none when game dont have a winner" in {

      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1)),
          (2, Player(id = 2))
        ),
        turns = 2
      )

      controller.getWinner() should be(None)
    }
    "undo should notify with error" in {
      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)
      val mockObserver = mock[Observer]

      (mockObserver.update _).expects(*, *).once()
      (mockUndoManager.undoStep _)
        .expects(*)
        .returns(Failure(new Exception("error")))

      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1)),
          (2, Player(id = 2))
        ),
        turns = 2
      )

      controller.add(mockObserver)
      controller.undo
      controller.errorMsg should be(Some("error"))
    }
    "saveField calls fileIO service" in {
      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)

      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1)),
          (2, Player(id = 2))
        ),
        turns = 2
      )

      (mockFileIO.save(_: FieldInterface)).expects(controller.field).once()
      controller.saveField
    }
    "loadField calls fileIO service" in {
      val controller = Controller(mockFileIO, mockUndoManager, mockCardProvider)

      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1)),
          (2, Player(id = 2))
        ),
        turns = 2
      )

      (mockFileIO.load _).expects().onCall(_ => Success(controller.field)).once()
      controller.loadField
    }
  }
}
