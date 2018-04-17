package main.scala

import scala.collection.mutable.ArrayBuffer

class Board() {
    
    var coords = ArrayBuffer[(Int, Int)]()
    val order = Array(9, 10, 11, 18, 17, 16, 15, 14, 7, 8, 1, 2, 3, 4, 5, 12, 13, 20, 19, 26, 25, 24, 23, 22)
    val pattern = Array("a", "b", "c", "A", "B", "C")
    val patternMap = Map("a" -> "A", "b" -> "B", "c" -> "C", "A" -> "a", "B" -> "b", "C" -> "c")
    
    for(j <- 0 to 4) {
        for(i <- 0 to 6) {
            coords.append((i,j))
        }
    }
    println(coords.mkString)
    
}