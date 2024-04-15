package hearthstoneMini
package model

import core.model.playerComponent.playerImpl.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import core.model.cardComponent.cardImpl.Card
import core.model.cardComponent.CardInterface

class PlayerSpec extends AnyWordSpec with Matchers {
  val testCards: List[Card] = List(
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test2", 2, 2, 2, "testEffect2", "testRarety2", 0, ""),
    Card("test3", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test4", 3, 3, 3, "testEffect3", "testRarety3", 0, ""),
    Card("test5", 1, 1, 1, "testEffect1", "testRarety1", 0, ""),
    Card("test6", 4, 4, 4, "testEffect4", "testRarety4", 0, "")
  )

  val graveyard: Array[CardInterface] = Array(
    Card("test1", 1, 1, 1, "testEffect1", "testRarety1", 0, "")
  )

  val testCardsPlayer: Player = Player("Player0", 0, deck = testCards)

  val player1: Player = Player("Player1", 1)
  val player2: Player = Player("Player2", 2)

  "A Player" when {
    "created should have default values" in {
      player1.name should be("Player1")
      player1.id should be(1)
      player1.hpValue should be(1)
      player1.maxHpValue should be(5)
      player1.manaValue should be(1)
      player1.maxManaValue should be(2)
      player1.hand.length should be(5)
      player1.deck.length should be(30)
      player1.field shouldBe a[Vector[Option[CardInterface]]]
    }
    "set a name" in {
      val player = player2.setName("TestPlayer2")
      player.name should be("TestPlayer2")
    }
  }

  "Player actions" when {
    "placing a card" in {
      val initialHandSize = player1.hand.size
      val newPlayer = player1.placeCard(2, 2)
      newPlayer.field(2).isDefined should be(true)
      newPlayer.hand.size should be(initialHandSize - 1)
    }
    "drawing a card" in {
      val initialHandSize = player1.hand.size
      val initialDeckSize = player1.deck.size
      val newPlayer = player1.drawCard()
      newPlayer.hand.size should be(initialHandSize + 1)
      newPlayer.deck.size should be(initialDeckSize - 1)
    }
    "reduce & increase hp" in {
      val player = player1.setHpValue(10)
      val newPlayer = player.reduceHp(4).increaseHp(2)
      newPlayer.hpValue should be(8)
    }
    "if hp is zero" in {
      val player = player1.setHpValue(0)
      player.isHpEmpty should be(true)
    }
    "reduce & increase mana" in {
      val player = player1.setManaValue(10)
      val newPlayer = player.reduceMana(5).increaseMana(2)
      newPlayer.manaValue should be(7)
    }
    "reset & increase mana" in {
      val player = player1.setManaValue(10).resetAndIncreaseMana()
      player.manaValue should be(11)
      player.maxManaValue should be(11)
    }
    "if mana is zero" in {
      val player = player1.setManaValue(0)
      player.isManaEmpty should be(true)
    }
  }

  "Serialisation should work correct" when {
    "ToJson" in {
      val player = Player(
        "Player",
        0,
        deck = testCards,
        hand = testCards.take(1)
      ).toJson.toString
      val expectedJson =
        """{"name":"Player","id":0,"hand":[{"card":{"id":"","name":"test1","manaCost":1,"attValue":1,"defenseValue":1,"effect":"testEffect1","rarity":"testRarety1"}}],"deck":[{"card":{"id":"","name":"test1","manaCost":1,"attValue":1,"defenseValue":1,"effect":"testEffect1","rarity":"testRarety1"}},{"card":{"id":"","name":"test2","manaCost":2,"attValue":2,"defenseValue":2,"effect":"testEffect2","rarity":"testRarety2"}},{"card":{"id":"","name":"test3","manaCost":1,"attValue":1,"defenseValue":1,"effect":"testEffect1","rarity":"testRarety1"}},{"card":{"id":"","name":"test4","manaCost":3,"attValue":3,"defenseValue":3,"effect":"testEffect3","rarity":"testRarety3"}},{"card":{"id":"","name":"test5","manaCost":1,"attValue":1,"defenseValue":1,"effect":"testEffect1","rarity":"testRarety1"}},{"card":{"id":"","name":"test6","manaCost":4,"attValue":4,"defenseValue":4,"effect":"testEffect4","rarity":"testRarety4"}}],"friedhof":[],"hpValue":1,"maxHpValue":5,"manaValue":1,"maxManaValue":2,"field":[null,null,null,null,null]}"""
      assert(player === expectedJson)
    }
    "FromJson" in {
      val json =
        Player("Player", 0, deck = testCards, hand = testCards.take(1)).toJson
      val fromJson = Player.fromJson(json)
      fromJson.id should be(0)
      fromJson.name should be("Player")
      fromJson.deck.length should be(6)
    }
    "ToXml" in {
      val player = Player(
        "Player",
        0,
        deck = testCards.take(2),
        hand = testCards.take(1),
        friedhof = graveyard
      ).toXml.toString()
      val expectedXml = """<player>
                          |      <name>
                          |        Player
                          |      </name>
                          |      <id>
                          |        0
                          |      </id>
                          |      <hpValue>
                          |        1
                          |      </hpValue>
                          |      <maxHpValue>
                          |        5
                          |      </maxHpValue>
                          |      <manaValue>
                          |        1
                          |      </manaValue>
                          |      <maxManaValue>
                          |        2
                          |      </maxManaValue>
                          |      <hand>
                          |        <card>
                          |            <id></id>
                          |            <name>test1</name>
                          |            <manaCost>1</manaCost>
                          |            <attValue>1</attValue>
                          |            <defenseValue>1</defenseValue>
                          |            <effect>testEffect1</effect>
                          |            <rarity>testRarety1</rarity>
                          |        </card>
                          |      </hand>
                          |      <deck>
                          |        <card>
                          |            <id></id>
                          |            <name>test1</name>
                          |            <manaCost>1</manaCost>
                          |            <attValue>1</attValue>
                          |            <defenseValue>1</defenseValue>
                          |            <effect>testEffect1</effect>
                          |            <rarity>testRarety1</rarity>
                          |        </card><card>
                          |            <id></id>
                          |            <name>test2</name>
                          |            <manaCost>2</manaCost>
                          |            <attValue>2</attValue>
                          |            <defenseValue>2</defenseValue>
                          |            <effect>testEffect2</effect>
                          |            <rarity>testRarety2</rarity>
                          |        </card>
                          |      </deck>
                          |      <friedhof>
                          |        <card>
                          |            <id></id>
                          |            <name>test1</name>
                          |            <manaCost>1</manaCost>
                          |            <attValue>1</attValue>
                          |            <defenseValue>1</defenseValue>
                          |            <effect>testEffect1</effect>
                          |            <rarity>testRarety1</rarity>
                          |        </card>
                          |      </friedhof>
                          |      <field>
                          |        <card> </card><card> </card><card> </card><card> </card><card> </card>
                          |      </field>
                          |    </player>""".stripMargin
      assert(player === expectedXml)
    }
    "FromXml" in {
      val xml = Player(
        "Player",
        0,
        deck = testCards.take(2),
        hand = testCards.take(1),
        friedhof = graveyard
      ).toXml
      val fromXml = Player.fromXml(xml)
      fromXml.name should be("Player")
      fromXml.id should be(0)
      fromXml.deck.length should be(2)
    }
  }
}
