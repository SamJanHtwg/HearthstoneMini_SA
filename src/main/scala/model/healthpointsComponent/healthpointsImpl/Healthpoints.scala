package hearthstoneMini
package model.healthpointsComponent.healthpointsImpl

import model.healthpointsComponent.HealthpointsInterface
import play.api.libs.json.*

import scala.xml.Node

object Healthpoints{
    def fromJson(json: JsValue): Healthpoints = Healthpoints(
        value = (json \ "value").get.toString.toInt,
        max = (json \ "max").get.toString.toInt
    )
    def fromXML(node: Node): Healthpoints = Healthpoints(
            value = (node \\ "value").head.text.toInt,
            max = (node \\ "max").head.text.toInt
        )
}
case class Healthpoints(value: Int = 0, max: Int = 30) extends HealthpointsInterface {
    override def increase(amount: Int): Healthpoints = if (amount + value > max) then copy(value = amount + value, max = amount + value) else copy(value = amount + value)
    override def decrease(amount: Int): Healthpoints = if (value - amount < 0) then copy(value = 0) else copy(value = value - amount)
    override def isEmpty: Boolean = value <= 0
    override def setVal(amount: Int): Healthpoints = copy(value = amount, amount)
    override def toString: String = value.toString

    override def toJson: JsValue = Json.obj(
        "value" -> Json.toJson(value),
        "max" -> Json.toJson(max)
    )

    override def toXML: Node =
        <Healpoints>
            <value>{value}</value>
            <max>{max}</max>
        </Healpoints>
}
