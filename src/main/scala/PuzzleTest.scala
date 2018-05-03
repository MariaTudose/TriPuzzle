package main.scala

import java.io._
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.collection.mutable.Map

@RunWith(classOf[JUnitRunner])
class PuzzleTest extends FlatSpec with Matchers {

    "Piece equals" should "return true when comparing rotated equal pieces" in {
        val board = new Board

        val piece1 = new Piece(board, (1, 1), (0, 0), (0, 0))
        piece1.setSides("a", "b", "c")

        val piece2 = new Piece(board, (1, 2), (0, 0), (0, 0))
        piece2.setSides("c", "a", "b")

        val success = piece1.equals(piece2)
        assert(success, "equals function doesn't return true")

    }

    "Board createPieces" should "create unique pieces" in {
        val board = new Board
        val pieceMap = board.createPieces()
        val pieces = pieceMap.map(x => x._2)
        for (piece <- pieces) {
            val success = pieces.filter(_ != piece).forall(p => !p.equals(piece))
            assert(success, "pieces are not unique")
        }
    }

    "Board checkIfSolved" should "return false for rotated pieces in correct locations" in {
        val board = new Board
        val pieceMap = board.createPieces()
        for ((pos, piece) <- pieceMap) {
            board.setPiece(piece, pos)
        }
        assert(!board.checkIfSolved(), "reported as solved even though pieces are rotated")
    }

}