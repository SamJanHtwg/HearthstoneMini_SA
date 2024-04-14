package core
package util.commands

import core.model.fieldComponent.FieldInterface

import scala.util.Try
import core.model.cardComponent.CardInterface

//noinspection UnitMethodIsParameterless,MutatorLikeMethodIsParameterless
trait CommandInterface {
  def doStep: Try[FieldInterface]
  def undoStep: Unit
  def redoStep: Unit
}
