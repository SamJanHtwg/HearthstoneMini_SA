package hearthstoneMini
package util.commands

import model.fieldComponent.FieldInterface

import scala.util.Try

//noinspection UnitMethodIsParameterless,MutatorLikeMethodIsParameterless
trait CommandInterface {
  def doStep: Try[FieldInterface]
  def undoStep: Unit
  def redoStep: Unit
  def checkConditions: Boolean
}
