package hearthstoneMini
package util.commands

import model.fieldComponent.FieldInterface

import scala.util.Try
import hearthstoneMini.model.cardComponent.CardInterface

//noinspection UnitMethodIsParameterless,MutatorLikeMethodIsParameterless
trait CommandInterface {
  def doStep: Try[FieldInterface]
  def undoStep: Unit
  def redoStep: Unit
}
