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
import scala.util.Try
import scala.util.Failure
import scala.util.Success

object Card {
  @nowarn
  given cardReads: Reads[Option[Card]] = (o: JsValue) => {
    (o \ "type").validate[String] match
      case JsSuccess("MINION", _) =>
        JsSuccess(
          Some(
            Card(
              name = (o \ "name").as[String].grouped(10).toList.head,
              (o \ "cost").as[Int],
              (o \ "attack").as[Int],
              (o \ "health").as[Int],
              "Effect",
              "Rarity",
              id = (o \ "id").as[String]
            )
          )
        )
      case JsSuccess(_, _) => JsSuccess(None)
  }

  def fromJson(json: JsValue): Card = {
    val jsonCard = json \ "card"
    Card(
      name = jsonCard("name").toString().replace("\"", ""),
      manaCost = jsonCard("manaCost").toString().toInt,
      attValue = jsonCard("attValue").toString.toInt,
      defenseValue = jsonCard("defenseValue").toString.toInt,
      effect = jsonCard("effect").toString.replace("\"", ""),
      rarity = jsonCard("rarity").toString.replace("\"", ""),
      id = jsonCard("id").toString.replace("\"", "")
    )
  }

  def fromXml(node: Node): Card = {
    Card(
      name = (node \ "name").text,
      manaCost = (node \ "manaCost").text.trim.toInt,
      attValue = (node \ "attValue").text.trim.toInt,
      defenseValue = (node \ "defenseValue").text.trim.toInt,
      effect = (node \ "effect").text,
      rarity = (node \ "rarity").text,
      id = (node \ "id").text
    )
  }
}

case class Card(
    name: String,
    manaCost: Int,
    attValue: Int,
    defenseValue: Int,
    effect: String,
    rarity: String,
    val attackCount: Int = 1,
    id: String
) extends CardInterface {
  override def toString: String =
    name + " (" + manaCost + ")" + "#" + "atk: " + attValue + "#def: "
      + defenseValue + "#" + effect + "#" + rarity

  override def reduceHP(amount: Int): Card =
    copy(defenseValue = defenseValue - amount)
  override def reduceAttackCount(): Card = copy(attackCount = attackCount - 1)
  override def resetAttackCount(): Card = copy(attackCount = 1)

  def toJson: JsValue = Json.obj(
    "card" -> Json.obj(
      "id" -> Json.toJson(id),
      "name" -> Json.toJson(name),
      "manaCost" -> Json.toJson(manaCost),
      "attValue" -> Json.toJson(attValue),
      "defenseValue" -> Json.toJson(defenseValue),
      "effect" -> Json.toJson(effect),
      "rarity" -> Json.toJson(rarity)
    )
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
