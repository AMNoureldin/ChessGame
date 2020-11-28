package sample;

import game.*;
import game.piece.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    private Piece promotionPiece;
    private boolean isFlipped;
    private boolean p1White = true;


    //TODO Design and implement main menu
    //TODO Find a way to display killed pieces on display
    //TODO Change game start resolution and make the board scale correctly

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
            if (move.isPromotion()){
                PieceCanvas promotedTo = aGame.getCanvas(move.getPromotedTo());
                Pane gridSpot = (Pane) getNodeFromGridPane(boardPane, move.getFinish().getX(), move.getFinish().getY());
                promotedTo.widthProperty().bind(gridSpot.widthProperty());
                promotedTo.heightProperty().bind(gridSpot.heightProperty());
            }
            startTurn();
        }

    }
    private void activatePieces(){
        ArrayList<Piece> pieces = aGame.getCurrentPlayerPieces();
        for (Piece piece : pieces){
            aGame.getCanvas(piece).setOnMouseClicked(selectPiece);
        }
    }
    private void deactivatePieces(){
        ArrayList<Piece> pieces = aGame.getCurrentPlayerPieces();
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
                int[] position = getTrueCoordinates(i, j);
                Pane pane = (Pane) getNodeFromGridPane(boardPane, position[0], position[1]);
                if (!cur.getPiece().isPresent()){
                    pane.setOnMouseClicked(selectSpot);
                }
                else{
                    Piece piece = cur.getPiece().get();
                    if (piece.isWhite() != isWhite){
                        pane.setOnMouseClicked(selectSpot);
                    }
                    else if(selected != null) {
                        if (selected.getPiece() instanceof King && piece instanceof Rook){
                            //pane.setOnMouseClicked(selectSpot);
                        }
                    }
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
                int[] position = getTrueCoordinates(i, j);
                Pane gridSpot =  (Pane) getNodeFromGridPane(boardPane, position[0], position[1]);
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
    // Takes Gridpane coordinates and translates them to game board coordinates
    private int[] getTrueCoordinates(int y, int x){
        int[] newCoord = new int[2];
        if (isFlipped) {
            newCoord[0] = y;
            newCoord[1] = x;
            return newCoord;
        }
        newCoord[0] = 7 - y;
        newCoord[1] = 7 - x;
        return newCoord;
    }

    private void pushCastleMove(MouseEvent event){
        //deactivateTiles();
        deactivatePieces();
        Pane startPane = (Pane) selected.getParent();
        Pane endPane = (Pane) ((PieceCanvas) event.getSource()).getParent();
        int[] startPosition = getTrueCoordinates( GridPane.getColumnIndex(startPane), GridPane.getRowIndex(startPane));
        int[] endPosition = getTrueCoordinates(GridPane.getColumnIndex(endPane), GridPane.getRowIndex(endPane));
        Spot start = aGame.getGameBoard().getSpot(startPosition[0], startPosition[1]);
        Spot end = aGame.getGameBoard().getSpot(endPosition[0], endPosition[1]);
        Move move = new Move(start, end, true);
        pushMove(move);
        event.consume();
    }
    private boolean isPromotion(Move move){
        Spot start = move.getStart();
        Spot end = move.getFinish();
        if (!start.getPiece().isPresent()) return false;
        Piece piece = start.getPiece().get();
        if (piece instanceof Pawn){
            if (piece.isWhite() && end.getY() == 7) return true;
            else if(!piece.isWhite() && end.getY() == 0) return true;
        }
        return false;
    }
    private Piece getPromotion(MouseEvent mouseEvent) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("promotion.fxml"));
        Parent root = fxmlLoader.load();
        Scene mainScene = new Scene(root);
        Stage stage = new Stage();
        Pane target = (Pane) mouseEvent.getSource();
        Bounds bounds = target.localToScreen(target.getBoundsInLocal());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setX(bounds.getMaxX());
        stage.setY(bounds.getMaxY());
        stage.setHeight(bounds.getHeight() * 2);
        stage.setWidth(bounds.getWidth() * 2);
        stage.setScene(mainScene);
        GridPane grid = (GridPane) root;
        drawPromotionGrid(grid);
        stage.showAndWait();
        return promotionPiece;
    }
    private void drawPromotionGrid(GridPane grid){
        for(int i=0; i < 2; i++){
            for(int j=0; j <2; j++){
                PieceCanvas piece ;
                Pane pane = (Pane) getNodeFromGridPane(grid, i, j);
                if (i == 0){
                    if (j == 0) piece = new PieceCanvas(pane.getWidth(), pane.getHeight(), new Queen(aGame.getCurrentPlayer().isWhite()));
                    else piece = new PieceCanvas(pane.getWidth(), pane.getHeight(), new Rook(aGame.getCurrentPlayer().isWhite()));
                }
                else{
                    if (j == 0) piece = new PieceCanvas(pane.getWidth(), pane.getHeight(), new Bishop(aGame.getCurrentPlayer().isWhite()));
                    else piece = new PieceCanvas(pane.getWidth(), pane.getHeight(), new Knight(aGame.getCurrentPlayer().isWhite()));
                }
                piece.heightProperty().bind(pane.heightProperty());
                piece.widthProperty().bind(pane.widthProperty());
                piece.draw();
                piece.setOnMouseClicked(selectPromotion);
                pane.getChildren().add(piece);
            }
        }

    }
    private EventHandler<MouseEvent> selectPromotion = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            PieceCanvas canvas = (PieceCanvas) event.getSource();
            promotionPiece = canvas.getPiece();
            canvas.getParent().getScene().getWindow().hide();
            event.consume();
        }
    };
    private EventHandler selectPiece = new EventHandler<MouseEvent>(){

        public void handle(MouseEvent event){
            if(selected != null) {
                Piece piece = ((PieceCanvas)event.getSource()).getPiece();
                if(selected.getPiece() instanceof King && piece instanceof Rook){
                    pushCastleMove(event);
                    selected = null;
                    return;
                }
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
            int[] sCoord = getTrueCoordinates(x0, y0);
            int[] eCoord = getTrueCoordinates(x1, y1);
            Spot start = gameBoard.getSpot(sCoord[0], sCoord[1]);
            Spot end = gameBoard.getSpot(eCoord[0], eCoord[1]);
            Move move = new Move(start, end, false);
            if(isPromotion(move)){
                try{
                    Piece promotion = getPromotion(mouseEvent);
                    move.setPromotion(promotion);
                }
                catch (Exception  e){
                    e.printStackTrace();
                }
            }
            selected = null;
            pushMove(move);

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
