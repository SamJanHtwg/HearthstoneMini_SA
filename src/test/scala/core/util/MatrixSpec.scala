package hearthstoneMini
package model

import core.util.matrix.Matrix
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MatrixSpec extends AnyWordSpec with Matchers {
  "Matrix" when {
    "empty" should {
      val matrix = new Matrix(20, 60, "|")
      val eol = sys.props("line.separator")
      "should have size and no Content" in {
        matrix.rowSize should be(20)
      }
      "should allow insert at col, row" in {
        val string: String = "Hello World!#Hello World!"
        val newMa = matrix.updateMatrix(0, 0, string.split("#").toList)
      }

    }
    "updateMatrix" should {
      val matrix = new Matrix(20, 60, "|")
      val eol = sys.props("line.separator")
      "allow insert at col, row" in {
        val string: String = "Hello World!#Hello World!"
        val newMa = matrix.updateMatrix(0, 0, string.split("#").toList)
        newMa.rows(0)(0) should be('H')
      }
      "allow insert of another matrix at col, row" in {
        val string: String = "Hello World!#Hello World!"
        val newMa = matrix.updateMatrix(0, 0, string.split("#").toList)
        val newMa2 = matrix.updateMatrix(0, 0, newMa)
        newMa2.rows(0)(0) should be('H')
      }
    }
    "toString" should {
      val matrix = new Matrix(20, 60, "|")
      val eol = sys.props("line.separator")
      "return a string representation of the matrix" in {
        matrix.toString should be(
          ("|" * 60 + eol) * 20
        )
      }
    }
  }
}
