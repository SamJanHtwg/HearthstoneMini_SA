package hearthstoneMini
package controller

package hearthstoneMini
package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import controller.component.controllerImpl.Controller
import util.Observer
import _root_.hearthstoneMini.model.cardComponent.cardImpl.Card
import _root_.hearthstoneMini.model.fieldComponent.fieldImpl.Field
import _root_.hearthstoneMini.model.playerComponent.playerImpl.Player
import _root_.hearthstoneMini.model.gamebarComponent.gamebarImpl.Gamebar
import _root_.hearthstoneMini.model.Move
import _root_.hearthstoneMini.model.healthpointsComponent.healthpointsImpl.Healthpoints


class ControllerSpec extends AnyWordSpec with Matchers {
  val testCards = List[Card](Card("test1", 1, 1, 1, "testEffect1", "testRarety1"),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1"), Card("test1", 1, 1, 1, "testEffect1", "testRarety1"),
        Card("test1", 1, 1, 1, "testEffect1", "testRarety1"))

  "The Controller" should {
    "have a default gametstate of GameState.PREGAME" in {
      val controller = Controller(Field(slotNum = 5,
        players = List[Player](Player(id = 1,
          gamebar = Gamebar(hand = testCards)).resetAndIncreaseMana(),
          Player(id = 2))))
      controller.gameState should be(GameState.CHOOSEMODE)
    }
    "place a card on field" in {
      val controller = Controller(Field(slotNum = 5,
        players = List[Player](Player(id = 1,
          gamebar = Gamebar(hand = testCards)).resetAndIncreaseMana(),
          Player(id = 2))))
      controller.placeCard(Move(2, 2))
      controller.field.players(0).fieldbar.cardArea.row(2).isDefined should be(true)
    }
    "draw a card" in {
      val controller = Controller(Field(slotNum = 5,
        players = List[Player](Player(id = 1,
          gamebar = Gamebar(hand = testCards)).resetAndIncreaseMana(),
          Player(id = 2))))
      controller.drawCard()
      controller.field.players(0).gamebar.hand.length should be(5)
    }
    "setting player names" in {
      val controller = Controller(Field(slotNum = 5,
        players = List[Player](Player(id = 1,
          gamebar = Gamebar(hand = testCards)).resetAndIncreaseMana(),
          Player(id = 2))))
      controller.setPlayerNames(Move(p1 = "Jan", p2 = "Sam"))
      controller.field.players(0).name should be("Jan")
      controller.field.players(1).name should be("Sam")
    }
    "attacking" in {
      val controller = Controller(Field(slotNum = 5,
        players = List[Player](Player(id = 1,
          gamebar = Gamebar(hand = testCards)).resetAndIncreaseMana(),
          Player(id = 2,gamebar = Gamebar(hand = testCards)))))
      controller.nextState()
      controller.nextState()
      controller.placeCard(Move(2, 2))
      controller.switchPlayer()
      controller.placeCard(Move(2, 2))
      controller.switchPlayer()
      controller.attack(Move(fieldSlotActive = 2, fieldSlotInactive = 2))
      controller.field.players(0).fieldbar.cardArea.row(3).isEmpty should be(true)
    }
    "switching player" in {
      val controller = Controller(Field(slotNum = 5,
        players = List[Player](Player(id = 1,
          gamebar = Gamebar(hand = testCards)).resetAndIncreaseMana(),
          Player(id = 2))))
      controller.setPlayerNames(Move(p2 = "Sam"))
      controller.switchPlayer()
      controller.field.players(0).name should be("Sam")
    }
    "do a direct attack" in {
      val controller = Controller(Field(slotNum = 5,
        players = List[Player](Player(id = 1,
          gamebar = Gamebar(hand = testCards)).resetAndIncreaseMana(),
          Player(id = 2))))
      controller.placeCard(Move(2, 2))
      controller.directAttack(Move(fieldSlotActive = 2))
      controller.field.players(1).gamebar.hp.value should be(30)
    }
    "undo step / redo step" in {
      val controller = Controller(Field(slotNum = 5,
        players = List[Player](Player(id = 1,
          gamebar = Gamebar(hand = testCards)).resetAndIncreaseMana(),
          Player(id = 2))))
      controller.drawCard()
      controller.undo
      controller.field.players(0).gamebar.hand.length should be(4)
      controller.redo
      controller.field.players(0).gamebar.hand.length should be(5)
    }
    "setStrategy should set a strategy based on input" in {
      val controller = Controller(Field(slotNum = 5,
        players = List[Player](Player(id = 1,
          gamebar = Gamebar(hand = testCards)).resetAndIncreaseMana(),
          Player(id = 2))))
      controller.setStrategy(Strategy.debug)
      controller.field.getPlayerById(1).gamebar.hp.value should be (100)
      controller.field.getPlayerById(1).gamebar.mana.value should be (100)
    }
    "should set gamestate to Exit" in {
      val controller = Controller(Field(slotNum = 5,
        players = List[Player](Player(id = 1,
          gamebar = Gamebar(hand = testCards)).resetAndIncreaseMana(),
          Player(id = 2))))
      controller.exitGame()
      controller.gameState should be(GameState.EXIT)
    }
    "should return the Winner when one player has 0 hp" in {
      val controller = Controller(Field(slotNum = 5,
        players = List[Player](Player(id = 1,
          gamebar = Gamebar(hand = testCards, hp = Healthpoints(1,1))).resetAndIncreaseMana(),
          Player(id = 2, gamebar = Gamebar(hand = testCards, hp = Healthpoints(1,1)))), turns = 2))
      controller.placeCard(Move(2, 2))
      controller.directAttack(Move(fieldSlotActive = 2))
      controller.getWinner().get should be(controller.field.players(0).name)
    }

  }
}