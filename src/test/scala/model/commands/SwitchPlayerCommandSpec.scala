package hearthstoneMini
package model.commands

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import core.controller.component.controllerImpl.Controller
import _root_.model.cardComponent.cardImpl.Card

import _root_.model.playerComponent.playerImpl.Player
import core.util.Observer
import _root_.model.fieldComponent.FieldInterface
import _root_.model.fieldComponent.fieldImpl.Field
import core.util.commands.commandImpl.SwitchPlayerCommand
import org.scalamock.scalatest.MockFactory
import _root_.model.playerComponent.PlayerInterface


class SwitchPlayerCommandSpec
    extends AnyWordSpec
    with Matchers {
  "A controller" should {
    "when switching players" in {
      val testField = Field(
        players = Map[Int, PlayerInterface](
          (
            1,
            Player(
              name = "test",
              id = 1,
            )
          ),
          (
            2,
            Player(
              name = "test",
              id = 2,
            )
          )
        )
      )
      val switchPlayer = new SwitchPlayerCommand(testField)
      val result = switchPlayer.doStep
      result.get.activePlayerId should be(2)
      switchPlayer.memento should be(testField)

    }
  }
}
