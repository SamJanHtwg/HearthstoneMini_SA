package hearthstoneMini
package util
import model.fieldComponent.FieldInterface

import scala.util.Try

trait Command {
  def doStep: Try[FieldInterface]
  def undoStep: Unit
  def redoStep: Unit
  def checkConditions: Boolean
}
