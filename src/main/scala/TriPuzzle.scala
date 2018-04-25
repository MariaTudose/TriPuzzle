package main.scala

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafx.scene.shape.Polygon
import scalafx.scene.paint.Color
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.StackPane
import scalafx.scene.Group
import scalafx.scene.text.Font
import scalafx.scene.control._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Menu
import scala.io._
import java.io._
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.event._
import javafx.scene.input._
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

object TriPuzzle extends JFXApp {

    val bg = new Color(240, 240, 216)
    val pieceColor = new Color(192, 216, 96)
    val pieceBorder = new Color(96, 120, 72)
    val gridColor = new Color(96, 72, 72)

    stage = new JFXApp.PrimaryStage {
        title.value = "Triangle Puzzle"
        width = 600
        height = 400
        scene = new Scene {
            fill = bg
            content = new Pane {

                startGame(children)

            }
        }
    }

    def drawMenu(pieceMap: Map[(Int, Int), Piece], children: ObservableList[Node], board: Board): Unit = {
        val menuBar = new MenuBar
        val fileMenu = new Menu("File")
        val saveItem = new MenuItem("Save")
        val loadItem = new MenuItem("Load")
        val exitItem = new MenuItem("Exit")
        fileMenu.items = List(saveItem, loadItem, new SeparatorMenuItem, exitItem)
        menuBar.menus = List(fileMenu)
        menuBar.prefWidth = 600
        children.add(menuBar)
        exitItem.onAction = (e: ActionEvent) => sys.exit()

        saveItem.setOnAction(new EventHandler[ActionEvent] {
            def handle(event: ActionEvent) {
                saveGame(board, pieceMap)
            }
        })

        loadItem.setOnAction(new EventHandler[ActionEvent] {
            def handle(event: ActionEvent) {
                loadGame(children)
            }
        })
    }

    def drawPieces(pieceMap: Map[(Int, Int), Piece], children: ObservableList[Node], board: Board) = {

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
                    val pieceX = g.localToScene(g.getBoundsInLocal).minX
                    val pieceY = g.localToScene(g.getBoundsInLocal).minY
                    piece.setCoord(pieceX, pieceY)
                    g.setLayoutX(g.getLayoutX + deltaX)
                    g.setLayoutY(g.getLayoutY + deltaY)
                    mousePosition = (event.getSceneX, event.getSceneY)
                }
            }

            children.addAll(triangleBoard, g)

        }
        for ((group, piece) <- trianglePieces) {
            group.toFront()
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
                                piece.setCoord(boardPiece.points(0), boardPiece.points(1) - 65)
                            } else if (piece.orientation == 0 && boardOrientation == 0) {
                                group.setLayoutX(group.getLayoutX + (boardPiece.points(0) - pieceX - 2))
                                group.setLayoutY(group.getLayoutY + (boardPiece.points(1) - pieceY + 2 + 65))
                                piece.setPos(key)
                                piece.setCoord(boardPiece.points(0), boardPiece.points(1))
                            }
                            board.setPiece(piece, key)
                            if (board.checkIfSolved()) {
                                val alert = new Alert(AlertType.Information) {
                                    initOwner(stage)
                                    title = "Congratulations"
                                    headerText = "You solved the puzzle correctly!"
                                }.showAndWait()
                            }
                        }
                    }
                }
            }
        }
        (boardPolygons, trianglePieces)
    }

    def saveGame(board: Board, pieceMap: Map[(Int, Int), Piece]) = {
        try {
            val writer = new PrintWriter("save.txt", "UTF-8")
            //writer.println(pieces.map(x => x.currentCoord))
            pieceMap.foreach {
                case (key, piece) =>
                    writer.print(key._1 + "," + key._2 + ",")
                    writer.print(piece.currentCoord._1 + "," + piece.currentCoord._2 + ",")
                    writer.print(piece.currentPos._1 + "," + piece.currentPos._2 + ",")
                    writer.print(piece.toString() + ";")
            }
            writer.close()
        } catch {
            case e: Exception => println(e)
        }
    }

    def loadGame(children: ObservableList[Node]) = {
        children.clear()
        val board = new Board
        var pieceMap = Map[(Int, Int), Piece]()
        try {
            val saveFile = Source.fromFile("save.txt").getLines()

            for (pieceData <- saveFile.next().split(";")) {
                val piece = pieceData.split(",")
                val targetPos = (piece(0).toInt, piece(1).toInt)
                val currentPos = (piece(4).toInt, piece(5).toInt)
                val currentCoord = (piece(2).toDouble, piece(3).toDouble)
                val pieceSides = piece(6)

                val p = new Piece(board, targetPos, currentPos, currentCoord)
                p.setSides(pieceSides(0).toString(), pieceSides(1).toString(), pieceSides(2).toString())
                if (currentPos != (0, 0)) {
                    board.setPiece(p, currentPos)
                }
                pieceMap(targetPos) = p

            }
            drawPieces(pieceMap, children, board)
            drawMenu(pieceMap, children, board)
        } catch {
            case e: Exception => println(e)
        }

    }

    def startGame(children: ObservableList[Node]) = {
        val board = new Board
        val pieceMap = board.createPieces()
        drawPieces(pieceMap, children, board)
        drawMenu(pieceMap, children, board)
        (board, pieceMap)
    }

}