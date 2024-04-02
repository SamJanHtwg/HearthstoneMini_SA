package hearthstoneMini
package util
import hearthstoneMini.util.commands.CommandInterface

//noinspection UnitMethodIsParameterless
class UndoManager {
  private var undoStack: List[CommandInterface] = Nil
  private var redoStack: List[CommandInterface] = Nil

  def canUndo: Boolean = undoStack.nonEmpty
  def canRedo: Boolean = redoStack.nonEmpty

  def doStep(command: CommandInterface): Unit = {
    undoStack = command :: undoStack
  }
  def undoStep: Unit = {
    undoStack match {
      case Nil =>
      case head :: stack =>
        head.undoStep
        undoStack = stack
        redoStack = head :: redoStack
    }
  }
  def redoStep: Unit = {
    redoStack match {
      case Nil =>
      case head :: stack =>
        head.redoStep
        redoStack = stack
        undoStack = head :: undoStack
    }
  }
}
