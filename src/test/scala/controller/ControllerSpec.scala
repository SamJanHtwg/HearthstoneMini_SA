package hearthstoneMini
package controller

import core.controller.{GameState, Strategy}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import core.controller.component.controllerImpl.Controller
import _root_.model.cardComponent.cardImpl.Card
import _root_.model.fieldComponent.fieldImpl.Field
import _root_.model.playerComponent.playerImpl.Player
import _root_.model.Move
import org.scalamock.scalatest.MockFactory
import _root_.model.fileIOComponent.FileIOInterface

class ControllerSpec extends AnyWordSpec with Matchers with MockFactory {
  val testCards: List[Card] = List[Card](
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, ""),
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 1, "")
  )

  "The Controller" should {
    "should have access to all game states" in {
      val allStates = GameState.values.toList
      assert(allStates.length == 5)
    }
    "have a default game state of GameState.PREGAME" in {
      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1).resetAndIncreaseMana()),
          (2, Player(id = 2))
        )
      )
      controller.gameState should be(GameState.CHOOSEMODE)
    }
    "place a card on field" in {
      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)

      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1, manaValue = 100, hand = testCards)),
          (2, Player(id = 2))
        ),
        turns = 3
      )
      controller.gameState = GameState.MAINGAME
      controller.placeCard(Move(2, 2))
      controller.field
        .players(controller.field.activePlayerId)
        .field(2)
        .isDefined should be(true)
    }
    "draw a card" in {
      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)
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
      controller.drawCard()
      controller.field
        .players(controller.field.activePlayerId)
        .hand
        .length should be(5)
    }
    "setting player names" in {
      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)
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
      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1, hand = testCards).resetAndIncreaseMana()),
          (2, Player(id = 2, hand = testCards))
        )
      )

      controller.nextState()
      controller.nextState()
      controller.placeCard(Move(2, 2))
      controller.switchPlayer()
      controller.placeCard(Move(2, 2))
      controller.switchPlayer()
      controller.attack(Move(fieldSlotActive = 2, fieldSlotInactive = 2))
      controller.field
        .players(controller.field.activePlayerId)
        .field(3)
        .isEmpty should be(true)
    }
    "switching player" in {
      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1, name = 1.toString).resetAndIncreaseMana()),
          (2, Player(id = 2, name = 2.toString))
        )
      )
      controller.switchPlayer()
      controller.field.players(controller.field.activePlayerId).name should be(
        "2"
      )
    }
    "do a direct attack" in {
      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1, manaValue = 100, hand = testCards)),
          (2, Player(id = 2, hpValue = 5))
        ),
        turns = 3
      )
      controller.gameState = GameState.MAINGAME
      controller.placeCard(Move(2, 2))
      controller.directAttack(Move(fieldSlotActive = 2))
      controller.field.players(2).hpValue should be(4)
    }
    "undo step / redo step" in {
      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1, hand = List.empty, deck = testCards)),
          (2, Player(id = 2))
        )
      )
      controller.drawCard()
      controller.canUndo should be(true)
      controller.undo
      controller.field
        .players(controller.field.activePlayerId)
        .hand
        .length should be(0)
      controller.canRedo should be(true)
      controller.redo
      controller.field
        .players(controller.field.activePlayerId)
        .hand
        .length should be(1)
    }
    "setStrategy should set a strategy based on input" in {
      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)
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
      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1).resetAndIncreaseMana()),
          (2, Player(id = 2))
        )
      )
      controller.exitGame()
      controller.gameState should be(GameState.EXIT)
    }
    "should return the Winner when one player has 0 hp" in {
      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)

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
      controller.gameState should be(GameState.WIN)
    }
    "should return none when game dont have a winner" in {
      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)
      controller.field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1)),
          (2, Player(id = 2))
        ),
        turns = 2
      )

      controller.getWinner() should be(None)
    }
    "should be able to save & load" in {
      val field = Field(
        players = Map[Int, Player](
          (1, Player(id = 1)),
          (2, Player(id = 2))
        )
      )

      val fileIoMock = mock[FileIOInterface]
      val controller = Controller(fileIoMock)
      val saved: Unit = controller.saveField
      val loaded: Unit = controller.loadField
      assert(saved === loaded)
    }
    "when unable to load, error should be triggered" in {
      // TODO: add testcase
    }
  }
}
