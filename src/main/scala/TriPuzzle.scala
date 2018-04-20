package main.scala

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafx.scene.shape.Polygon
import scalafx.scene.paint.Color
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.StackPane
import scalafx.scene.Group
import scalafx.scene.control.Label
import scalafx.scene.text.Font
import javafx.event.EventHandler
import javafx.scene.input._
import scala.util.Random
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

object TriPuzzle extends JFXApp {
    stage = new JFXApp.PrimaryStage {
        title.value = "Triangle Puzzle"
        width = 600
        height = 400
        scene = new Scene {
            val bg = new Color(240, 240, 216)
            fill = bg
            content = new Pane {
                val canvas = new Canvas(600, 400);
                children.add(canvas)
                val gc = canvas.getGraphicsContext2D();

                //260, 300

                val pieceColor = new Color(192, 216, 96)
                val pieceBorder = new Color(96, 120, 72)
                val gridColor = new Color(96, 72, 72)

                val xpoints = Array(75.0, 225.0, 300.0, 225.0, 75.0, 0.0)
                val ypoints = Array(50.0, 50.0, 180.0, 310.0, 310.0, 180.0)

                val board = new Board
                val pieceMap = createPieces(board)

                var boardPolygons = new ArrayBuffer[(Polygon, (Int, Int), Int)]()
                var trianglePieces = new ArrayBuffer[(Group, Piece)]()

                for ((key, piece) <- pieceMap) {
                    val x = piece.currentCoord._1
                    val y = piece.currentCoord._2

                    val xBoard = key._1 * 37.5
                    val yBoard = key._2 * 65 + 50

                    val triangleBoard = new Polygon
                    boardPolygons.append((triangleBoard, key, piece.orientation))
                    triangleBoard.setStroke(gridColor)
                    triangleBoard.setFill(bg)

                    val trianglePiece = new Polygon
                    trianglePiece.setFill(pieceColor)
                    trianglePiece.setStroke(pieceBorder)
                    trianglePiece.setStrokeWidth(2)

                    val labelA = new scalafx.scene.control.Label(piece.a)
                    labelA.layoutX = x + 22
                    labelA.layoutY = y + 25

                    val labelC = new scalafx.scene.control.Label(piece.c)
                    labelC.layoutX = x + 44
                    labelC.layoutY = y + 25

                    val labelB = new scalafx.scene.control.Label(piece.b)
                    labelB.layoutX = x + 33

                    if (piece.orientation == 1) {
                        trianglePiece.getPoints.addAll(x, y + 65.0, x + 37.5, y, x + 75.0, y + 65.0)
                        triangleBoard.getPoints.addAll(xBoard, yBoard + 65.0, xBoard + 37.5, yBoard, xBoard + 75.0, yBoard + 65.0)
                        labelB.layoutY = y + 50
                    } else {
                        trianglePiece.getPoints.addAll(x, y, x + 75, y, x + 37.5, y + 65.0)
                        triangleBoard.getPoints.addAll(xBoard, yBoard, xBoard + 75, yBoard, xBoard + 37.5, yBoard + 65.0)
                        labelB.layoutY = y
                    }

                    val g = new Group(trianglePiece, labelA, labelB, labelC)
                    trianglePieces.append((g, piece))

                    g.onMouseClicked = new EventHandler[MouseEvent] {
                        def handle(event: MouseEvent) {
                            if (event.getButton == MouseButton.SECONDARY) {
                                val temp = labelA.getText
                                labelA.text = labelB.getText
                                labelB.text = labelC.getText
                                labelC.text = temp
                            }
                        }
                    }

                    var mousePosition = (0.0, 0.0)
                    g.onMousePressed = new EventHandler[MouseEvent] {
                        def handle(event: MouseEvent) {
                            mousePosition = (event.getSceneX, event.getSceneY)
                            g.toFront()
                        }
                    }

                    g.onMouseDragged = new EventHandler[MouseEvent] {
                        def handle(event: MouseEvent) {
                            val deltaX = event.getSceneX - mousePosition._1
                            val deltaY = event.getSceneY - mousePosition._2
                            g.setLayoutX(g.getLayoutX + deltaX)
                            g.setLayoutY(g.getLayoutY + deltaY)
                            mousePosition = (event.getSceneX, event.getSceneY)
                        }
                    }

                    children.addAll(g, triangleBoard)

                }

                for ((group, piece) <- this.trianglePieces) {
                    group.onMouseReleased = new EventHandler[MouseEvent] {
                        def handle(event: MouseEvent) {
                            val mouseX = event.getSceneX
                            val mouseY = event.getSceneY
                            for ((boardPiece, key, boardOrientation) <- boardPolygons) {
                                if (boardPiece.contains(mouseX, mouseY)) {

                                    val pieceX = group.localToScene(group.getBoundsInLocal).minX
                                    val pieceY = group.localToScene(group.getBoundsInLocal).maxY                       

                                    if (piece.orientation == 1 && boardOrientation == 1) {
                                        group.setLayoutX(group.getLayoutX + (boardPiece.points(0) - pieceX - 2))
                                        group.setLayoutY(group.getLayoutY + (boardPiece.points(1) - pieceY + 2))
                                        piece.setPos(key)
                                    } else if (piece.orientation == 0 && boardOrientation == 0) {
                                        group.setLayoutX(group.getLayoutX + (boardPiece.points(0) - pieceX - 2))
                                        group.setLayoutY(group.getLayoutY + (boardPiece.points(1) - pieceY + 2 + 65))
                                        piece.setPos(key)
                                    }
                                }
                            }
                        }
                    }
                }

            }

        }
    }

    def createPieces(board: Board): Map[(Int, Int), Piece] = {
        val r = new Random
        var pieceMap = Map[(Int, Int), Piece]()
        var pieceUnique = false

        for (i <- board.order) {
            pieceUnique = false
            while (!pieceUnique) {
                val piece = new Piece(board.coords(i), (0, 0), (r.nextInt(220) + 300, r.nextInt(320)))
                piece.createSides(board, i, pieceMap)
                if (pieceMap.forall(!_._2.equals(piece))) {
                    pieceMap(board.coords(i)) = piece
                    pieceUnique = true
                }
            }

        }
        return pieceMap
    }
}