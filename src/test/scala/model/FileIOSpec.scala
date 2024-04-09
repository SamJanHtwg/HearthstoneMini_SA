package model

import hearthstoneMini.model.cardComponent.CardInterface
import hearthstoneMini.model.cardComponent.cardImpl.Card
import hearthstoneMini.model.fieldComponent.fieldImpl.Field
import hearthstoneMini.model.fileIOComponent.{FileIOInterface, xmlIOImpl}
import hearthstoneMini.model.fileIOComponent.jsonIOImpl.FileIO
import hearthstoneMini.model.playerComponent.playerImpl.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.xml.Node

class FileIOSpec extends AnyWordSpec with Matchers {
  val testCards: List[Card] = List(
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test2", 2, 2, 2, "testEffect2", "testRarety2", 0, ""),
  )

  val graveyard: Array[CardInterface] = Array(
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, "")
  )

  val testCardsPlayer1: Player = Player("Player1", 1, deck = testCards, friedhof = graveyard)
  val testCardsPlayer2: Player = Player("Player2", 2, deck = testCards, friedhof = graveyard)

  val field: Field = Field(players = Map[Int, Player](
    (1, testCardsPlayer1),
    (2, testCardsPlayer2)
  ))

  val field2: Node = field.toXML

  val fileIO = new FileIO()

  "Should be possible to save and load a field" when {
    "saved & loaded field should be identical " in {
      val saved: Unit = FileIO().save(field)
      val loaded: Unit = FileIO().load
      assert(saved === loaded)
    }
  }
}
