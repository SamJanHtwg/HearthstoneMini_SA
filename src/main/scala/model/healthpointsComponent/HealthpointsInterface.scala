package hearthstoneMini
package model.healthpointsComponent

import model.healthpointsComponent.healthpointsImpl.Healthpoints
import play.api.libs.json.JsValue
import scala.xml.Node

trait HealthpointsInterface {
  val value: Int
  val max: Int
  def increase(amount: Int): Healthpoints 
  def decrease(amount: Int): Healthpoints 
  def isEmpty: Boolean
  def setVal(amount: Int): Healthpoints
  def toJson: JsValue
  def toXML: Node
}



