package hearthstoneMini
package model.manaComponent.manaImpl

import model.manaComponent.ManaInterface
import play.api.libs.json.*

import scala.xml.Node

object Mana{
    def fromJson(json: JsValue): Mana =
        Mana(
            value = (json \ "value").get.toString.toInt,
            max = (json \ "max").get.toString.toInt
    )
    def fromXML(node: Node): Mana =
        Mana(
            value = (node \\ "value").head.text.toInt,
            max = (node \\ "max").head.text.toInt
        )
}
case class Mana(value: Int = 1, max: Int = 1) extends ManaInterface {

    def increase(amount: Int): Mana = if (amount + value > max) then copy(value = max) else copy(value = value + amount)
    def resetAndIncrease(): Mana = copy(value = max + 1, max = max + 1)
    def decrease(amount: Int): Mana = if (value - amount < 0) then copy(value = 0) else copy(value = value - amount)
    def isEmpty: Boolean = value <= 0
    def setVal(amount: Int): Mana = copy(value = amount, amount)
    override def toString = value.toString

    override def toJson: JsValue = Json.obj(
        "value" -> Json.toJson(value),
        "max" -> Json.toJson(max))

    override def toXML: Node =
        <Mana>
            <value>{value}</value>
            <max>{max}</max>
        </Mana>
}
