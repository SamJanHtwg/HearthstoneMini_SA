package model

import model.cardComponent.CardInterface
import model.cardComponent.cardImpl.Card
import model.fieldComponent.fieldImpl.Field
import model.fileIOComponent.xmlIOImpl.FileIO as xmlImport
import model.fileIOComponent.jsonIOImpl.FileIO as jsonImport
import model.playerComponent.playerImpl.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.xml.Node

class FileIOSpec extends AnyWordSpec with Matchers {
  val testCards: List[Card] = List(
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test2", 2, 2, 2, "testEffect2", "testRarety2", 0, "")
  )

  val graveyard: Array[CardInterface] = Array(
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, "")
  )

  val testCardsPlayer1: Player =
    Player("Player1", 1, deck = testCards, friedhof = graveyard)
  val testCardsPlayer2: Player =
    Player("Player2", 2, deck = testCards, friedhof = graveyard)

  val field: Field = Field(players =
    Map[Int, Player](
      (1, testCardsPlayer1),
      (2, testCardsPlayer2)
    )
  )

  val field2: Node = field.toXML

  val fileIOJson = new jsonImport
  val fileIOXml = new xmlImport

  "Should be possible to save and load a field" when {
    "with json variant" in {
      val saved: Unit = fileIOJson.save(field)
      val loaded: Unit = fileIOJson.load
      assert(saved === loaded)
    }
    "with xml variant" in {
      val saved: Unit = fileIOXml.save(field)
      val loaded: Unit = fileIOXml.load
    }
  }
}
