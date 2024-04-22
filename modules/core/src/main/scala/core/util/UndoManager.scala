package core
package util
import core.util.commands.CommandInterface
import model.fieldComponent.FieldInterface
import akka.http.scaladsl.common.StrictForm.Field
import scala.util.Try
import scala.util.Failure

//noinspection UnitMethodIsParameterless
class UndoManager {
  private var undoStack: List[CommandInterface] = Nil
  private var redoStack: List[CommandInterface] = Nil

  def canUndo: Boolean = undoStack.nonEmpty
  def canRedo: Boolean = redoStack.nonEmpty

  def doStep(command: CommandInterface): Unit = {
    undoStack = command :: undoStack
  }

  def undoStep(currentField: FieldInterface): Try[FieldInterface] = Try(
    undoStack match {
      case Nil => throw new Exception("No more undo steps available")
      case head :: stack =>
        undoStack = stack
        redoStack = head :: redoStack
        head.undoStep(currentField)
    }
  )

  def redoStep(currentField: FieldInterface): Try[FieldInterface] = Try(
    redoStack match {
      case Nil => throw new Exception("No more redo steps available")
      case head :: stack =>
        redoStack = stack
        undoStack = head :: undoStack
        head.redoStep(currentField)
    }
  )
}
