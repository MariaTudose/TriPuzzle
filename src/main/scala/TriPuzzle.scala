package main.scala

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafx.scene.text.Text
import scalafx.scene.shape.Polygon
import scalafx.scene.paint.Color
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.StackPane
import scalafx.scene.Group
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import scala.util.Random
import scala.collection.mutable.Map

object TriPuzzle extends JFXApp {
    stage = new JFXApp.PrimaryStage {
        title.value = "Triangle Puzzle"
        width = 600
        height = 400
        scene = new Scene {
            fill = Color.AliceBlue
            content = new Pane {
                val canvas = new Canvas(600, 400);
                children.add(canvas)
                val gc = canvas.getGraphicsContext2D();

                //260, 300

                val xpoints = Array(75.0, 225.0, 300.0, 225.0, 75.0, 0.0)
                val ypoints = Array(50.0, 50.0, 180.0, 310.0, 310.0, 180.0)

                gc.strokePolygon(xpoints, ypoints, 6)

                gc.strokeLine(75, 50, 225, 310)
                gc.strokeLine(225, 50, 75, 310)

                gc.strokeLine(0, 180, 300, 180)
                gc.strokeLine(37.5, 115, 262.5, 115)
                gc.strokeLine(37.5, 245, 262.5, 245)

                gc.strokeLine(150, 50, 37.5, 245)
                gc.strokeLine(150, 50, 262.5, 245)

                gc.strokeLine(37.5, 115, 150, 310)
                gc.strokeLine(262.5, 115, 150, 310)

                val board = new Board
                val pieceMap = createPieces(board)

                for ((key, piece) <- pieceMap) {
                    //val x = piece.currentPos._1
                    //val y = piece.currentPos._2

                    val x = key._1 * 37.5
                    val y = key._2 * 65 + 50

                    val polygon = new Polygon
                    polygon.setFill(Color.Gray)
                    polygon.setStroke(Color.Crimson)

                    val labelA = new scalafx.scene.control.Label(piece.a)
                    labelA.layoutX = x + 20
                    labelA.layoutY = y + 25

                    val labelC = new scalafx.scene.control.Label(piece.c)
                    labelC.layoutX = x + 45
                    labelC.layoutY = y + 25

                    val labelB = new scalafx.scene.control.Label(piece.b)
                    labelB.layoutX = x + 33

                    if (piece.orientation == 1) {
                        polygon.getPoints.addAll(x, y + 65.0, x + 37.5, y, x + 75.0, y + 65.0)
                        labelB.layoutY = y + 50
                    } else {
                        polygon.getPoints.addAll(x, y, x + 75, y, x + 37.5, y + 65.0)
                        labelB.layoutY = y
                    }

                    val g = new Group(polygon, labelA, labelB, labelC)

                    var mousePosition = (0.0, 0.0)

                    g.onMousePressed = new EventHandler[MouseEvent] {
                        def handle(event: MouseEvent) {
                            mousePosition = (event.getSceneX, event.getSceneY)
                        }
                    }

                    g.onMouseDragged = new EventHandler[MouseEvent] {
                        def handle(event: MouseEvent) {
                            val deltaX = event.getSceneX - mousePosition._1
                            val deltaY = event.getSceneY - mousePosition._2
                            piece.setPos(event.getSceneX, event.getSceneY)
                            //println(event.getSceneX)
                            //println(event.getSceneY)
                            g.setLayoutX(g.getLayoutX + deltaX)
                            g.setLayoutY(g.getLayoutY + deltaY)
                            mousePosition = (event.getSceneX, event.getSceneY)
                        }
                    }

                    children.add(g)

                    gc.setStroke(Color.Crimson)
                    gc.setFill(Color.Gray)

                    //gc.strokePolygon(xpos, ypos, 3)
                    //gc.fillPolygon(xpos, ypos, 3)

                }

            }

        }
    }

    def createPieces(board: Board): Map[(Int, Int), Piece] = {
        val r = new Random
        var pieceMap = Map[(Int, Int), Piece]()

        for (i <- board.order) {
            val piece = new Piece(board.coords(i), (r.nextInt(220) + 300, r.nextInt(320)))
            piece.getSides(board, i, pieceMap)
            pieceMap(board.coords(i)) = piece

            println(piece)
            println(board.coords(i))

        }
        return pieceMap
    }
}