package main.scala

import scala.collection.mutable.Map
import scala.util.Random

class Piece(targetPos: (Int, Int), var currentPos: (Double, Double)) {

    // a: left side, b: up or down side, c: right side
    var a = ""
    var b = ""
    var c = ""
    var pieces = Array(a, b, c)
    var rotation = 0
    var orientation = 0

    // orientation: 0 = down, 1 = up
    if (targetPos._1 % 2 == 0 && targetPos._2 % 2 != 0) orientation = 1
    else if (targetPos._1 % 2 != 0 && targetPos._2 % 2 == 0) orientation = 1

    def setSides(a: String, b: String, c: String) = {
        this.a = a
        this.b = b
        this.c = c
        pieces = Array(a, b, c)
    }

    def equals(another: Piece): Boolean = {
        for (i <- 0 until 3) {
            if(this.pieces(i) == another.pieces(0) && this.pieces((i + 1)%3) == another.pieces(1) && this.pieces((i + 2)%3) == another.pieces(2)) return true
        }
        false
    }

    def getSides(board: Board, i: Int, pieceMap: Map[(Int, Int), Piece]) = {
        val x = this.targetPos._1
        val y = this.targetPos._2
        val r = new Random
        var aSide = board.pattern(r.nextInt(6))
        var bSide = board.pattern(r.nextInt(6))
        var cSide = board.pattern(r.nextInt(6))

        var deltaY = 0

        if (orientation == 1) deltaY = 1 else deltaY = -1

        if (pieceMap.keySet.exists(_ == (x - 1, y))) {
            aSide = board.patternMap(pieceMap(x - 1, y).c)
        }
        if (pieceMap.keySet.exists(_ == (x + 1, y))) {
            cSide = board.patternMap(pieceMap(x + 1, y).a)
        }
        if (pieceMap.keySet.exists(_ == (x, y + deltaY))) {
            bSide = board.patternMap(pieceMap(x, y + deltaY).b)
        }

        this.setSides(aSide, bSide, cSide)
    }

    def setPos(x: Double, y: Double) = {
        currentPos = (x, y)
    }

    override def toString = a + b + c

}