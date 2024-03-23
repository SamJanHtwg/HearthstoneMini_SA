package hearthstoneMini
package model.matrixComponent.matrixImpl

import model.matrixComponent.MatrixInterface

case class Matrix[T](rows: Vector[Vector[String]]) extends MatrixInterface:
  def this(rowSize: Int, colSize: Int, filling: String) = this(Vector.tabulate(rowSize, colSize) { (row, col) => filling })
  val rowSize: Int = rows.size
  val colSize: Int = rows(0).length

  override def updateMatrix(rowStart: Int, colStart: Int, stringList: List[String]): Matrix[String] = copy(rows.patch(rowStart, stringList.zipWithIndex.map((string1, index) =>
    rows(rowStart + index).patch(colStart, string1.toVector.asInstanceOf[IterableOnce[String]], (string1.replaceAll("(\\u001b\\[)\\d{0,3}(;)?\\d*.", "")).length)), stringList.length))

  override def updateMatrixWithMatrix(rowStart: Int, colStart: Int, insertMatrix: Matrix[String]): Matrix[String] = copy(rows.patch(rowStart, insertMatrix.rows.zipWithIndex.map((vector,index) =>
    rows(rowStart + index).patch(colStart, vector, vector.length)), insertMatrix.rows.length))

  override def toString: String = rows.map(_.mkString).mkString("", "\n", "\n")