package main.scala

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.util.Random

class Board() {

    var coords = ArrayBuffer[(Int, Int)]()
    var pieceMap = Map[(Int, Int), Piece]()
    var pieces = ArrayBuffer[Piece]()
    // The order variable tells the best order to traverse the board coordinates for creating pieces 
    val order = Array(9, 10, 11, 18, 17, 16, 15, 14, 7, 8, 1, 2, 3, 4, 5, 12, 13, 20, 19, 26, 25, 24, 23, 22)
    val pattern = Array("a", "b", "c", "A", "B", "C")
    val patternMap = Map("a" -> "A", "b" -> "B", "c" -> "C", "A" -> "a", "B" -> "b", "C" -> "c")

    for (j <- 0 to 4) {
        for (i <- 0 to 6) {
            coords.append((i, j))
        }
    }

    def createPieces(): Map[(Int, Int), Piece] = {
        val r = new Random
        var originalPieceMap = Map[(Int, Int), Piece]()
        var pieceUnique = false

        for (i <- this.order) {
            pieceUnique = false
            while (!pieceUnique) {
                val piece = new Piece(this, this.coords(i), (0, 0), (r.nextInt(220) + 300, r.nextInt(290) + 30))
                piece.createSides(originalPieceMap, i)
                if (originalPieceMap.forall(!_._2.equals(piece))) {
                    for(i <- 0 to r.nextInt(2) + 1) piece.rotate()
                    originalPieceMap(this.coords(i)) = piece
                    pieces.append(piece)
                    pieceUnique = true
                }
            }
        }
        return originalPieceMap
    }

    def setPiece(piece: Piece, pos: (Int, Int)) = {
        pieceMap(pos) = piece
    }

    def checkIfSolved(): Boolean = {
        if (pieceMap.keys.size == 24) {
            for (((x, y), piece) <- pieceMap) {
                var aMatch = true
                var bMatch = true
                var cMatch = true
                if (piece.neighborFound(pieceMap, 0, x, y)) aMatch = piece.a == patternMap(pieceMap(x - 1, y).c)
                if (piece.neighborFound(pieceMap, 1, x, y)) cMatch = piece.c == patternMap(pieceMap(x + 1, y).a)
                if (piece.neighborFound(pieceMap, 2, x, y)) bMatch = piece.b == patternMap(pieceMap(x, y + piece.deltaY).b)
                if (!(aMatch && bMatch && cMatch)) return false
            }
            return true
        }
        return false
    }

}