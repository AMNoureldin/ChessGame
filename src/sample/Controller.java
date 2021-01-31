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
import java.util.HashMap;
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
    private HashMap<Piece, PieceCanvas> pieceCanvases;

    //TODO Add game timing functionality

    // Intialize game controller with player names.
    public void setupGame(String p1Name, String p2Name){
        this.p1Name.setText(p1Name);
        this.p2Name.setText(p2Name);
        Random rand = new Random();
        isFlipped = false;
        p1White = rand.nextInt() % 2 == 0;
        pieceCanvases = new HashMap<>();
    }
    // Event handler that starts the game.
    @FXML
    private void startGame() throws ExecutionException, InterruptedException {
        aGame = new Game(new HumanPlayer(true, boardPane), new HumanPlayer(false, boardPane), boardPane);
        Status status = aGame.checkStatus();
        drawInitialBoard();
        Player currentPlayer = aGame.getCurrentPlayer();
        drawPlayerLogos();
        startTurn();
    }
    @FXML
    private void flipBoard(){
        isFlipped = !isFlipped;
        startTurn();
    }

    // Initialize game interface with logos for the colors of each player.
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
        Pane wPane = p1White ? player1Logo : player2Logo;
        Pane bPane = p1White ? player2Logo : player1Logo;
        wPane.getChildren().add(wCanvas);
        bPane.getChildren().add(bCanvas);
    }
    // Initializes and draws the canvases representing the pieces.
    private void drawInitialBoard(){
        Board board = aGame.getGameBoard();
        //ArrayList<PieceCanvas> wPieces = new ArrayList<>();
        //ArrayList<PieceCanvas> bPieces = new ArrayList<>();
        for (int i=0; i < 8; i++){
            for(int j=0; j<8; j++){
                Spot boardSpot = board.getSpot(i, j);
                Pane gridSpot = (Pane) getNodeFromGridPane(boardPane, i, j);
                if (boardSpot.getPiece().isPresent()) {
                    double x = gridSpot.getWidth();
                    double y = gridSpot.getHeight();
                    Piece curPiece = boardSpot.getPiece().get();
                    PieceCanvas canvas = new PieceCanvas(x, y, curPiece);
                    canvas.widthProperty().bind(gridSpot.widthProperty());
                    canvas.heightProperty().bind(gridSpot.heightProperty());
                    canvas.draw();
                    gridSpot.getChildren().add(canvas);
                    pieceCanvases.put(curPiece, canvas);
                    //if (curPiece.isWhite()) wPieces.add(canvas);
                    //else bPieces.add(canvas);
                }
                //pane.setOnMouseClicked(selectSpot);
            }
        }
        //wPlayer.setPieces(wPieces);
        //bPlayer.setPieces(bPieces);
    }
    // Main handler for the game controller
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
                GridPane p1Info = (GridPane) player1Logo.getParent();
                GridPane p2Info = (GridPane) player2Logo.getParent();
                if(currentPlayer.isWhite() == p1White){
                    p1Info.setBorder(new Border(new BorderStroke(Color.DARKRED,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
                    p2Info.setBorder(null);
                }
                else{
                    p2Info.setBorder(new Border(new BorderStroke(Color.DARKRED,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
                    p1Info.setBorder(null);
                }
                activatePieces();
            }
        }

    }
    // Updates score displays.
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
    // Communicates selected move to game model
    private void pushMove(Move move){
        boolean valid = aGame.validateMove(move);
        if (!valid){ // If move is not valid allow the player to select a new move
            deactivateTiles();
            activatePieces();
        }
        else{ // else we advance the turn
            if (move.isPromotion()){ // if Move is a promotion we create and add the canvas of the new piece to the active set
                PieceCanvas promotedTo = new PieceCanvas(10, 10, move.getPromotedTo());
                pieceCanvases.put(move.getPromotedTo(), promotedTo);
                Pane gridSpot = (Pane) getNodeFromGridPane(boardPane, move.getFinish().getX(), move.getFinish().getY());
                promotedTo.widthProperty().bind(gridSpot.widthProperty());
                promotedTo.heightProperty().bind(gridSpot.heightProperty());
            }
            startTurn();
        }

    }
    // Activate pieces of current player for selection for a move.
    private void activatePieces(){
        ArrayList<Piece> pieces = aGame.getCurrentPlayerPieces();
        for (Piece piece : pieces){
            pieceCanvases.get(piece).setOnMouseClicked(selectPiece);
        }
    }
    // Deactivates the pieces of current player so they cannot be selected.
    private void deactivatePieces(){
        ArrayList<Piece> pieces = aGame.getCurrentPlayerPieces();
        for (Piece piece : pieces){
            pieceCanvases.get(piece).setOnMouseClicked(null);
        }
    }
    // Deactivates board tiles and opponents so they cannot be selected.
    private void deactivateTiles(){
        for (int i=0; i < 8; i++){
            for (int j=0; j < 8; j++){
                Pane pane = (Pane) getNodeFromGridPane(boardPane, i, j);
                pane.setOnMouseClicked(null);
            }
        }
    }
    // Activate empty tiles and the pieces of opponent player for selection for a move.
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


    // Draws active pieces to the board and killed pieces to the sides of the board.
    private void drawBoard(){
        Board board = aGame.getGameBoard();
        // Drawing the game board
        for (int i=0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Spot boardSpot = board.getSpot(i, j);
                int[] position = getTrueCoordinates(i, j);
                Pane gridSpot =  (Pane) getNodeFromGridPane(boardPane, position[0], position[1]);
                gridSpot.getChildren().clear();
                if (boardSpot.getPiece().isPresent()){
                    PieceCanvas canvas = pieceCanvases.get(boardSpot.getPiece().get());
                    gridSpot.getChildren().add(canvas);;
                }
                gridSpot.setBorder(null);
            }
        }
        // Drawing killed pieces
        ArrayList<Piece> wKills = aGame.getPlayerKills(isFlipped);
        ArrayList<Piece> bKills =  aGame.getPlayerKills(!isFlipped);
        lKills.getChildren().clear();
        rKills.getChildren().clear();
        for(Piece piece : wKills){
            PieceCanvas pieceCanvas = pieceCanvases.get(piece);
            VBox piecePane = new VBox();
            piecePane.getChildren().add(pieceCanvas);
            piecePane.setAlignment(Pos.TOP_CENTER);
            //VBox.setVgrow(piecePane, Priority.ALWAYS);
            lKills.getChildren().add(0, piecePane);
        }
        for(Piece piece : bKills){
            PieceCanvas pieceCanvas = pieceCanvases.get(piece);
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
    // Deals with applying a castle move to the model
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
    // Deals with applying a promotion move to the model
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
    // Displays promotion menu then returns the selected piece.
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
    // Draws the promotion menu
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
    // Event handler for promotion piece selection
    private EventHandler<MouseEvent> selectPromotion = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            PieceCanvas canvas = (PieceCanvas) event.getSource();
            promotionPiece = canvas.getPiece();
            canvas.getParent().getScene().getWindow().hide();
            event.consume();
        }
    };

    // Event handler to select piece to move.
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
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
            selected = canvas;
            activateTiles();
            event.consume();

        }
    };



    // Event handler for selecting destination spot.
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
            Pane pane = (Pane) selected.getParent();
            pane.setBorder(null);
            selected = null;
            pushMove(move);

        }
    };

    // Extracts a specific node from gridpane coordinates.
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
}
