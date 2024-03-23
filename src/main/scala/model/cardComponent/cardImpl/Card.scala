package hearthstoneMini
package model.cardComponent.cardImpl

import com.fasterxml.jackson.databind.JsonSerializable
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import model.cardComponent.CardInterface
import model.matrixComponent.matrixImpl.Matrix
import play.api.libs.json.*

import scala.annotation.nowarn
import scala.collection.View.Empty
import scala.xml.Node
import hearthstoneMini.model.fieldComponent.fieldImpl.FieldObject

object Card {
    @nowarn
    given cardReads: Reads[Option[Card]] = (o:JsValue) => {
        (o \ "type").validate[String] match
            case JsSuccess("MINION", _) => JsSuccess(Some(Card(name = (o \ "name").as[String].grouped(10).toList.head,
                (o \ "cost").as[Int], (o \ "attack").as[Int], (o \ "health").as[Int],
                "Effect", "Rarity", id = (o \ "id").as[String])))
            case JsSuccess(_, _) => JsSuccess(None)
    }

    def fromJSON(json: JsValue): Option[Card] = {
        val jsonObj = (json \ "card").get
        jsonObj.toString.replace("\"", "") match
            case "none" => None
            case _ => Some(
                Card(
                    name = (jsonObj \ "name").get.toString.replace("\"", ""),
                    manaCost = (jsonObj \ "manaCost").get.toString().toInt,
                    attValue = (jsonObj \ "attValue").get.toString.toInt,
                    defenseValue = (jsonObj \ "defenseValue").get.toString.toInt,
                    effect = (jsonObj \ "effect").get.toString.replace("\"", ""),
                    rarity = (jsonObj \ "rarity").get.toString.replace("\"", ""),
                    id = (jsonObj \ "id").get.toString.replace("\"", "")
                )
            )
    }


    def fromXML(node: Node): Option[Card] = {
        val nodeObj = node \\ "card"
        nodeObj.head.text match {
            case "none" => None
            case _ => Some(
                Card(
                    name = (node \\ "name").head.text,
                    manaCost = (node \\ "manaCost").head.text.toInt,
                    attValue = (node \\ "attValue").head.text.toInt,
                    defenseValue = (node \\ "defenseValue").head.text.toInt,
                    effect = (node \\ "effect").head.text,
                    rarity = (node \\ "rarity").head.text,
                    id = (node \\ "id").head.text
                )
            )
        }
    }
}
case class Card(val name: String,
           val manaCost: Int, val attValue: Int, val defenseValue: Int,
           val effect: String, val rarity: String,
           var attackCount: Int  = 1, val id: String)
  extends CardInterface {
    override def toString: String = name + " (" + manaCost + ")" + "#" + "atk: " + attValue + "#def: "
      + defenseValue + "#" + effect + "#" + rarity
    override def toMatrix: Matrix[String] = new Matrix[String](FieldObject.standartCardHeight,
        FieldObject.standartCardWidth, " ").updateMatrix(0, 0,
        toString().split("#").toList)
    override def reduceHP(amount: Int): Card = copy(defenseValue = defenseValue - amount)
    override def reduceAttackCount(): Card = copy(attackCount = attackCount - 1)
    override def resetAttackCount(): Card = copy(attackCount = 1)

    def toJson: JsValue = Json.obj(
        "id" -> id,
        "name" -> name,
        "manaCost" -> manaCost,
        "attValue" -> attValue,
        "defenseValue" -> defenseValue,
        "effect" -> effect,
        "rarity" -> rarity
    )
    def toXML: Node =
        <card>
            <id>{id}</id>
            <name>{name}</name>
            <manaCost>{manaCost.toString}</manaCost>
            <attValue>{attValue.toString}</attValue>
            <defenseValue>{defenseValue.toString}</defenseValue>
            <effect>{effect}</effect>
            <rarity>{rarity}</rarity>
        </card>
}

case class EmptyCard(val name: String = "yolo", val manaCost: Int = 0,
                val attValue: Int = 0, val defenseValue: Int = 0, val effect: String = "", val rarity: String = "",
                var attackCount: Int = 0, val id: String)
  extends CardInterface {

  override def toJson: JsValue = ???

  override def toXML: Node = ???

    override def toMatrix: Matrix[String] = new Matrix[String](FieldObject.standartCardHeight,
        FieldObject.standartCardWidth, " ")

    override def reduceHP(amount: Int): EmptyCard = copy(defenseValue = defenseValue - amount)
    override def reduceAttackCount(): EmptyCard = copy(attackCount = attackCount - 1)
    override def resetAttackCount(): CardInterface = copy(attackCount = 0)
}
