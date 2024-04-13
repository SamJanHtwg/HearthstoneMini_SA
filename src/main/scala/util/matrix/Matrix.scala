package hearthstoneMini
package util.matrix

case class Matrix(rows: Vector[Vector[String]]):
  def this(rowSize: Int, colSize: Int, filling: String) =
    this(Vector.tabulate(rowSize, colSize) { (row, col) => filling })
  val rowSize: Int = rows.size
  val colSize: Int = rows(0).length

  def updateMatrix(
      rowStart: Int,
      colStart: Int,
      stringList: Iterable[String]
  ): Matrix = copy(
    rows.patch(
      rowStart,
      stringList.zipWithIndex.map((string1, index) =>
        rows(rowStart + index).patch(
          colStart,
          string1.toVector.asInstanceOf[IterableOnce[String]],
          string1.replaceAll("(\\u001b\\[)\\d{0,3}(;)?\\d*.", "").length
        )
      ),
      stringList.size
    )
  )

  def updateMatrix(
      rowStart: Int,
      colStart: Int,
      insertMatrix: Matrix
  ): Matrix = copy(
    rows.patch(
      rowStart,
      insertMatrix.rows.zipWithIndex.map((vector, index) =>
        rows(rowStart + index).patch(colStart, vector, vector.length)
      ),
      insertMatrix.rows.length
    )
  )

  override def toString: String = rows.map(_.mkString).mkString("", "\n", "\n")
