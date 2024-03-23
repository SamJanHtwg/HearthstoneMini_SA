package hearthstoneMini
package model.matrixComponent

import model.matrixComponent.matrixImpl.Matrix

trait MatrixInterface {
  val rowSize: Int
  val colSize: Int

  def updateMatrix(rowStart: Int, colStart: Int, stringList: List[String]): Matrix[String]
  def updateMatrixWithMatrix(rowStart: Int, colStart: Int, insertMatrix: Matrix[String]): Matrix[String]
}
