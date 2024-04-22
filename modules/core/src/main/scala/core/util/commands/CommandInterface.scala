package core
package util.commands

import model.fieldComponent.FieldInterface

import scala.util.Try
import model.cardComponent.CardInterface
import akka.http.scaladsl.common.StrictForm.Field

//noinspection UnitMethodIsParameterless,MutatorLikeMethodIsParameterless
trait CommandInterface {
  var memento: FieldInterface
  val field: FieldInterface

  def doStep: Try[FieldInterface]
  def undoStep(currentField: FieldInterface): FieldInterface = {
    val oldField = memento
    memento = currentField
    oldField
  }
  def redoStep(currentField: FieldInterface): FieldInterface = {
    val oldField = memento
    memento = currentField
    oldField
  }
}
