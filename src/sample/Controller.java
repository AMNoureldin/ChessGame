package sample;

import game.*;
import game.piece.Piece;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

public class Controller {
    @FXML GridPane boardPane;
    @FXML Label gameStatus;
    @FXML VBox lKills;
    @FXML VBox rKills;
    @FXML Pane player1Logo;
    @FXML Pane player2Logo;
    @FXML Label p1Score;
    @FXML Label p2Score;
    @FXML Label p1Name;
    @FXML Label p2Name;
    private final static String wKingURL = "file:misc/Chess_klt60.png";
    private final static String bKingURL = "file:misc/Chess_kdt60.png";
    private PieceCanvas selected;
    private Game aGame;
    private Move nextMove;
    private boolean isFlipped;
    private boolean p1White = true;



    //TODO Add game timing functionality
    //TODO Make assignment of white and black random


    public void setupGame(String p1Name, String p2Name){
        this.p1Name.setText(p1Name);
        this.p2Name.setText(p2Name);
        Random rand = new Random();
        isFlipped = false;
        p1White = rand.nextInt() % 2 == 0;
    }
    @FXML
    private void startGame() throws ExecutionException, InterruptedException {
        aGame = new Game(new HumanPlayer(true, boardPane), new HumanPlayer(false, boardPane), boardPane);
        Status status = aGame.checkStatus();
        drawBoard();
        Player currentPlayer = aGame.getCurrentPlayer();
        drawPlayerLogos();
        startTurn();
    }
    private void drawPlayerLogos(){
        Canvas wCanvas = new Canvas(player1Logo.getWidth(), player1Logo.getHeight());
        wCanvas.heightProperty().bind(player1Logo.heightProperty());
        wCanvas.widthProperty().bind(player1Logo.widthProperty());
        Canvas bCanvas = new Canvas(player2Logo.getWidth(), player2Logo.getHeight());
        bCanvas.heightProperty().bind(player2Logo.heightProperty());
        bCanvas.widthProperty().bind(player2Logo.widthProperty());
        Image wImage = new Image(wKingURL);
        Image bImage = new Image(bKingURL);
        GraphicsContext gc = wCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, wCanvas.getWidth(), wCanvas.getHeight());
        gc.drawImage(wImage, 0, 0, wCanvas.getWidth(), wCanvas.getHeight());
        gc = bCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, bCanvas.getWidth(), bCanvas.getHeight());
        gc.drawImage(bImage, 0, 0, bCanvas.getWidth(), bCanvas.getHeight());
        player1Logo.getChildren().add(wCanvas);
        player2Logo.getChildren().add(bCanvas);
    }
    private void startTurn(){
        drawBoard();
        Status status = aGame.checkStatus();
        gameStatus.setText(status.toString());
        Player currentPlayer = aGame.getCurrentPlayer();
        updateScore();
        if (status == Status.ONGOING){
            if(currentPlayer instanceof ComputerPlayer){
                aGame.validateMove(currentPlayer.getMove());
                startTurn();
            }
            else{
                activatePieces();
            }
        }

    }
    private void updateScore(){
        int wScore =  aGame.getScore(true);
        int bScore = aGame.getScore(false);
        if (wScore - bScore >= 0){
            p1Score.setText(Integer.toString(0));
            p2Score.setText(Integer.toString(bScore - wScore));
        }
        else {
            p1Score.setText(Integer.toString(wScore - bScore));
            p2Score.setText(Integer.toString(0));
        }
    }
    private void pushMove(Move move){
        boolean valid = aGame.validateMove(move);
        if (!valid){
            deactivateTiles();
            activatePieces();

        }
        else{
            startTurn();
        }

    }
    private void activatePieces(){
        ArrayList<Piece> pieces = aGame.getCurrentPlayer().getPieces();
        for (Piece piece : pieces){
            aGame.getCanvas(piece).setOnMouseClicked(selectPiece);
        }
    }
    private void deactivatePieces(){
        ArrayList<Piece> pieces = aGame.getCurrentPlayer().getPieces();
        for (Piece piece : pieces){
            aGame.getCanvas(piece).setOnMouseClicked(null);
        }
    }
    private void deactivateTiles(){
        for (int i=0; i < 8; i++){
            for (int j=0; j < 8; j++){
                Pane pane = (Pane) getNodeFromGridPane(boardPane, i, j);
                pane.setOnMouseClicked(null);
            }
        }
    }

    private void activateTiles(){
        boolean isWhite = aGame.getCurrentPlayer().isWhite();
        for (int i=0; i < 8; i++){
            for (int j=0; j < 8; j++){
                Spot cur = aGame.getGameBoard().getSpot(i, j);
                Pane pane = (Pane)
                        (isFlipped ? getNodeFromGridPane(boardPane, i, j) : getNodeFromGridPane(boardPane, i, 7-j));
                if (!cur.getPiece().isPresent()){
                    pane.setOnMouseClicked(selectSpot);
                }
                else if (cur.getPiece().get().isWhite() != isWhite){
                    pane.setOnMouseClicked(selectSpot);
                }
            }
        }
    }
    private void endGame(){

    }
    private void runGame(){
        Status status = aGame.checkStatus();
        if (status != Status.ONGOING) endGame();
        drawBoard();
        Player currentPlayer = aGame.getCurrentPlayer();


    }
    private void drawBoard(){
        Board board = aGame.getGameBoard();
        for (int i=0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Spot boardSpot = board.getSpot(i, j);
                Pane gridSpot =  isFlipped ? (Pane) getNodeFromGridPane(boardPane, i, j) :
                        (Pane)getNodeFromGridPane(boardPane, i, 7 - j);
                gridSpot.getChildren().clear();
                if (boardSpot.getPiece().isPresent()){
                    PieceCanvas canvas = aGame.getCanvas(boardSpot.getPiece().get());
                    gridSpot.getChildren().add(canvas);;
                }
                gridSpot.setBorder(null);
            }
        }
        ArrayList<Piece> wKills = isFlipped ? aGame.getwKills() : aGame.getbKills();
        ArrayList<Piece> bKills = isFlipped ? aGame.getbKills() : aGame.getwKills();
        lKills.getChildren().clear();
        rKills.getChildren().clear();
        for(Piece piece : wKills){
            PieceCanvas pieceCanvas = aGame.getCanvas(piece);
            VBox piecePane = new VBox();
            piecePane.getChildren().add(pieceCanvas);
            piecePane.setAlignment(Pos.TOP_CENTER);
            //VBox.setVgrow(piecePane, Priority.ALWAYS);
            lKills.getChildren().add(0, piecePane);
        }
        for(Piece piece : bKills){
            PieceCanvas pieceCanvas = aGame.getCanvas(piece);
            VBox piecePane = new VBox();
            piecePane.getChildren().add(pieceCanvas);
            rKills.setAlignment(Pos.BOTTOM_CENTER);
            //VBox.setVgrow(piecePane, Priority.ALWAYS);
            rKills.getChildren().add(0, piecePane);
        }

    }

    private EventHandler selectPiece = new EventHandler<MouseEvent>(){

        public void handle(MouseEvent event){
            if(selected != null) {
                Pane pane = (Pane) selected.getParent();
                pane.setBorder(null);
            }
            PieceCanvas canvas = (PieceCanvas) event.getSource();
            Pane pane = (Pane) canvas.getParent();
            pane.setBorder(new Border(new BorderStroke(Color.DARKRED,
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            selected = canvas;
            activateTiles();
            event.consume();

        }
    };

    @FXML
    private void flipBoard(){
        isFlipped = !isFlipped;
        startTurn();
    }
    private EventHandler<MouseEvent> selectSpot = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (selected == null || !(mouseEvent.getSource() instanceof Pane)) return;
            deactivatePieces();
            Pane endPane = (Pane) mouseEvent.getSource();
            Pane startPane = (Pane) selected.getParent();
            int y1 = GridPane.getRowIndex(endPane);
            int x1 = GridPane.getColumnIndex(endPane);
            int y0 = GridPane.getRowIndex(startPane);
            int x0 = GridPane.getColumnIndex(startPane);
            Board gameBoard = aGame.getGameBoard();
            Spot start = isFlipped ? gameBoard.getSpot(x0, y0) : gameBoard.getSpot(x0, 7 - y0);
            Spot end = isFlipped ? gameBoard.getSpot(x1, y1) : gameBoard.getSpot(x1, 7 - y1);
            Move move = new Move(start, end, false);
            pushMove(move);
            selected = null;
        }
    };
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
}
