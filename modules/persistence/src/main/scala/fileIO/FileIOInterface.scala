package persistence
package fileIO

import model.fieldComponent.FieldInterface
import scala.util.Try

trait FileIOInterface {
  def load: Try[FieldInterface]
  def save(field: FieldInterface): Unit
}
