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
  }
}
