package hearthstoneMini
package model.cardareaComponent.cardareaImpl

import model.cardareaComponent.CardAreaInterface
import model.cardComponent.cardImpl.Card
import play.api.libs.json.*
import scala.xml.Node
import hearthstoneMini.model.cardComponent.CardInterface

object Cardarea {
    def fromXML(node: Node): Cardarea = {

        Cardarea(
            row = (node \\ "entry").map(card => Card.fromXML(card)).toVector
        )
    }

    def fromJson(json: JsValue): Cardarea = {
        val jsonObj = json \ "cardarea"
        Cardarea(
            row = (jsonObj\ "row").validate[List[JsValue]].get.map(card => Card.fromJSON(card)).toVector
        )
    }
}

case class Cardarea(row: Vector[Option[CardInterface]]) extends CardAreaInterface:
    def this(size: Int, filling: Option[CardInterface]) = this(Vector.tabulate(size) { (row) => filling })
    override val size: Int = row.size
    override def slot(slotNum: Int): Option[CardInterface] = row(slotNum)
    override def replaceSlot(slotNum: Int, card: Option[CardInterface]): CardAreaInterface = copy(row.updated(slotNum, card))
    override def reduceDefVal(slotNum: Int, amount: Int): CardAreaInterface = copy(row.updated(slotNum, 
        Some(row(slotNum).get.reduceHP(amount))))
    override def reduceAttackCount(slotNum: Int): CardAreaInterface = copy(row.updated(slotNum, 
        Some(row(slotNum).get.reduceAttackCount())))
    override def resetAttackCount(): CardAreaInterface = copy(
        {
            var old = row
            row.zipWithIndex.foreach((card, index) => old = old.updated(index, card.fold(None)(card =>
                Some(card.resetAttackCount()))))
            old
        }
    )
    override def toJson: JsValue = Json.obj(
        "row" -> row.map(card => card.fold(Json.obj("card" -> "none"))(card => Json.obj("card" -> card.toJson))).toList
    )
    override def toXML: Node =
        <Cardarea>
            <row>
                {row.map(card =>
                <entry>
                    {card.fold(<card>{"none"}</card>)(card => <card>{card.toXML}</card>)}
                </entry>
            )}
            </row>
    </Cardarea>

