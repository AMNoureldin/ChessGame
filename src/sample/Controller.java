package sample;

import game.*;
import game.piece.Piece;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.concurrent.*;

public class Controller {
    @FXML GridPane boardPane;
    private String wKingURL = "file:src/sample/Chess_klt60.png";
    private PieceCanvas selected;
    private Game aGame;
    private Move nextMove;

    //TODO Design and implement main menu
    //TODO Find a way to display killed pieces on display
    //TODO Change game start resolution and make the board scale correctly


//    public void testButton(){
//        Pane pane = (Pane) boardPane.getChildren().get(4);
//        double x = pane.getWidth();
//        double y = pane.getHeight();
//        Canvas canvas = new Canvas(x, y);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        Image icon = new Image(wKingURL);
//        gc.drawImage(icon, 0, 0, x, y);
//        pane.getChildren().add(canvas);
//    }
//    public void testButton(){
//        Pane pane = (Pane) boardPane.getChildren().get(4);
//        double x = pane.getWidth();
//        double y = pane.getHeight();
//        Image icon = new Image(wKingURL);
//        PieceCanvas canvas = new PieceCanvas(x, y, icon);
//        canvas.widthProperty().bind(pane.widthProperty());
//        canvas.heightProperty().bind(pane.heightProperty());
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        gc.drawImage(icon, 0, 0, x, y);
//        pane.getChildren().add(canvas);
//    }
    //@FXML
    /*private void drawBoard(){
        Game game = new Game(new HumanPlayer(true, boardPane), new HumanPlayer(false, boardPane), boardPane);
        Runnable runnable = game::play;
        Thread gameThread = new Thread(runnable);
        gameThread.start();
    }*/
    @FXML
    private void startGame() throws ExecutionException, InterruptedException {
        aGame = new Game(new HumanPlayer(true, boardPane), new HumanPlayer(false, boardPane), boardPane);
        Status status = aGame.checkStatus();
        adrawBoard();
        Player currentPlayer = aGame.getCurrentPlayer();
        startTurn();
    }
    private void startTurn(){
        adrawBoard();
        Status status = aGame.checkStatus();
        Player currentPlayer = aGame.getCurrentPlayer();
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
                Pane pane = (Pane) getNodeFromGridPane(boardPane, i, j);
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
        adrawBoard();
        Player currentPlayer = aGame.getCurrentPlayer();


    }
    private void adrawBoard(){
        Board board = aGame.getGameBoard();
        for (int i=0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Spot boardSpot = board.getSpot(i, j);
                Pane gridSpot = (Pane) getNodeFromGridPane(boardPane, i, j);
                gridSpot.getChildren().clear();
                if (boardSpot.getPiece().isPresent()){
                    PieceCanvas canvas = aGame.getCanvas(boardSpot.getPiece().get());
                    gridSpot.getChildren().add(canvas);;
                }
                gridSpot.setBorder(null);
            }
        }

    }
    /*@FXML
    public void drawBoard(){
        if (aGame != null) return;
        aGame = new Game(new HumanPlayer(true), new HumanPlayer(false), boardPane);
        Board board = aGame.getGameBoard();
        for (int i=0; i < 8; i++){
            for(int j=0; j<8; j++){
                Spot cur = board.getSpot(i, j);
                Pane pane = (Pane) getNodeFromGridPane(boardPane, i, j);
                if (cur.getPiece().isPresent()) {
                    double x = pane.getWidth();
                    double y = pane.getHeight();
                    Piece curPiece = cur.getPiece().get();
                    Image icon = new Image(curPiece.getIconURL());
                    PieceCanvas canvas = new PieceCanvas(x, y, icon);
                    canvas.widthProperty().bind(pane.widthProperty());
                    canvas.heightProperty().bind(pane.heightProperty());
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.drawImage(icon, 0, 0, x, y);
                    canvas.setOnMouseClicked(selectPiece);
                    pane.getChildren().add(canvas);
                }
                pane.setOnMouseClicked(selectSpot);

            }
        }
    }*/
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

    @FXML private EventHandler<MouseEvent> selectSpot = new EventHandler<MouseEvent>() {
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
            Spot start = aGame.getGameBoard().getSpot(x0, y0);
            Spot end = aGame.getGameBoard().getSpot(x1, y1);
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
