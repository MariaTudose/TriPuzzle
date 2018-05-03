package main.scala

import scala.collection.mutable.Map
import scala.util.Random

class Piece(var board: Board, targetPos: (Int, Int), var currentPos: (Int, Int), var currentCoord: (Double, Double)) {

    // a: left side, b: up or down side, c: right side
    var a = ""
    var b = ""
    var c = ""
    var pieces = Array(a, b, c)
    var rotation = 0
    var orientation = 0
    var deltaY = 0

    // orientation: 0 = down, 1 = up
    if (targetPos._1 % 2 == 0 && targetPos._2 % 2 != 0) orientation = 1
    else if (targetPos._1 % 2 != 0 && targetPos._2 % 2 == 0) orientation = 1

    if (orientation == 1) deltaY = 1 else deltaY = -1

    def setSides(a: String, b: String, c: String) = {
        this.a = a
        this.b = b
        this.c = c
        pieces = Array(a, b, c)
    }

    def rotate() = {
        val temp = a
        a = b
        b = c
        c = temp
    }

    def equals(another: Piece): Boolean = {
        for (i <- 0 until 3) {
            if (this.pieces(i) == another.pieces(0) && this.pieces((i + 1) % 3) == another.pieces(1) && this.pieces((i + 2) % 3) == another.pieces(2)) return true
        }
        false
    }

    def createSides(pieceMap: Map[(Int, Int), Piece], i: Int) = {
        val x = this.targetPos._1
        val y = this.targetPos._2
        val r = new Random
        var aSide = board.pattern(r.nextInt(6))
        var bSide = board.pattern(r.nextInt(6))
        var cSide = board.pattern(r.nextInt(6))

        if (neighborFound(pieceMap, 0, x, y)) aSide = board.patternMap(pieceMap(x - 1, y).c)
        if (neighborFound(pieceMap, 1, x, y)) cSide = board.patternMap(pieceMap(x + 1, y).a)
        if (neighborFound(pieceMap, 2, x, y)) bSide = board.patternMap(pieceMap(x, y + deltaY).b)

        this.setSides(aSide, bSide, cSide)
    }

    def neighborFound(pieceMap: Map[(Int, Int), Piece], dir: Int, x: Int, y: Int): Boolean = {
        dir match {
            case 0 => return pieceMap.keySet.exists(_ == (x - 1, y))
            case 1 => return pieceMap.keySet.exists(_ == (x + 1, y))
            case 2 => pieceMap.keySet.exists(_ == (x, y + deltaY))
            case _ => false
        }
    }

    def setPos(pos: (Int, Int)) = {
        currentPos = pos
    }

    def setCoord(coord: (Double, Double)) = {
        currentCoord = coord
    }

    override def toString = a + b + c

}