package hearthstoneMini
package model.fileIOComponent

import model.fieldComponent.FieldInterface

trait FileIOInterface {
  def load: FieldInterface
  def save(field: FieldInterface): Unit
}
