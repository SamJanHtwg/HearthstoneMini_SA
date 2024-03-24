package hearthstoneMini
package model.gamebarComponent.gamebarImpl

import model.cardComponent.cardImpl.Card
import model.fieldComponent.fieldImpl.{Field, FieldObject}
import model.fieldComponent.FieldInterface
import model.matrixComponent.matrixImpl.Matrix

import scala.collection.View.Empty
import scala.compiletime.ops.string
import model.gamebarComponent.GamebarInterface
import play.api.libs.json.*


import scala.xml.Node
import hearthstoneMini.util.CardProvider
import hearthstoneMini.model.cardComponent.CardInterface

object Gamebar {
    def fromJson(json: JsValue): Gamebar = Gamebar(
        hand = (json \ "hand").validate[List[JsValue]].get.map(card => Card.fromJSON(card).get),
        deck = (json \ "deck").validate[List[JsValue]].get.map(card => Card.fromJSON(card).get),
        friedhof = (json \ "friedhof").validate[List[JsValue]].get.map(card => Card.fromJSON(card).get).toArray,
    )
    def fromXML(node: Node): Gamebar = Gamebar(
        hand = (node \\"hand" \\ "entry").map(card => Card.fromXML(card).get).toList,
        deck = (node \\"deck" \\ "entry").map(card => Card.fromXML(card).get).toList,
        friedhof = (node \\"friedhof" \\ "entry").map(card => Card.fromXML(card).get).toArray,
    )
}
//noinspection DuplicatedCode
case class Gamebar(hand: List[CardInterface] = new CardProvider("/json/cards.json").getCards(5),
                   deck: List[CardInterface] = new CardProvider("/json/cards.json").getCards(30),
                   friedhof: Array[CardInterface] = Array[CardInterface]()) extends GamebarInterface{

    def removeCardFromHand(slot: Int): Gamebar = copy(hand = hand.filter(_ != hand(slot)))
    def addCardToHand(card: Option[CardInterface]): Gamebar = copy(hand = hand.appended(card.get))
    def addCardToFriedhof(card: Option[CardInterface]): Gamebar = card match {
        case Some(_) => copy(friedhof = friedhof.appended(card.get))
        case None => this
    }
    def drawCard(): Gamebar = copy(hand = hand.appended(deck.head), deck = deck.filter(_ != deck.head))
    def handAsMatrix(): Matrix[String] = {
        var tmpMatrix =  new Matrix[String](FieldObject.standartCardHeight, FieldObject.standartFieldWidth, " ")
        hand.zipWithIndex.foreach((elem,index) => tmpMatrix = tmpMatrix.updateMatrixWithMatrix(0, FieldObject.standartSlotWidth * index + 1, hand(index).toMatrix))
        tmpMatrix
    }

    def toMatrix: Matrix[String] = new Matrix[String](FieldObject.standartGameBarHeight, FieldObject.standartFieldWidth, " ")
      .updateMatrix(0, FieldObject.standartFieldWidth - 22, List[String]("Deck: " + deck.length + "  Friedhof: " + friedhof.length))
      .updateMatrixWithMatrix(1, 0, handAsMatrix())
      .updateMatrix(6, 0, List[String]("-" * FieldObject.standartFieldWidth))

    def toJson: JsValue =
        Json.obj(
            "deck" -> deck.map(card => Json.obj("card" -> card.toJson)),
            "hand" -> hand.map(card => Json.obj("card" -> card.toJson)),
            "friedhof" -> friedhof.map(card => Json.obj("card" -> card.toJson))
        )
    def toXML: Node =
        <Player>
            <hand>{hand.map(card => <entry>{card.toXML}</entry>)}</hand>
            <deck>{deck.map(card => <entry>{card.toXML}</entry>)}</deck>
            <friedhof>{friedhof.map(card => <entry>{card.toXML}</entry>)}</friedhof>
        </Player>
}
