package game;

import game.piece.*;
import game.piece.Piece;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Game {
    private Player wPlayer, bPlayer;
    private Board board;
    private static Move move;
    private ArrayList<Piece> wKills;
    private ArrayList<Piece> bKills;
    private int wScore, bScore;

    private GridPane boardPane;
    private HashMap<Piece, PieceCanvas> active;
    private HashMap<Piece, PieceCanvas> kills;
    private PieceCanvas selected;
    private boolean wTurn;
    private Spot wKingPos, bKingPos;
    private LinkedList<Move> movesList;
    //TODO Test all pieces for correct movement
    //TODO Run full game then declare winner.
    public Game(Player wPlayer, Player bPlayer, GridPane boardPane) {
        this.wPlayer = wPlayer;
        this.bPlayer = bPlayer;
        board = Board.getBoard();
        wScore = 39;
        bScore = 39;
        wKills = new ArrayList<>();
        bKills = new ArrayList<>();
        movesList = new LinkedList<>();
        this.boardPane = boardPane;
        active = new HashMap<>();
        kills = new HashMap<>();
        initStd();
    }

    private void initStd(){
        for (int i=0; i < 8; i++){
            board.setSpot(i, 1, new Pawn(true));
            board.setSpot(i, 6, new Pawn(false));
            if (i == 0 || i == 7){
                board.setSpot(i, 0, new Rook(true));
                board.setSpot(i, 7, new Rook(false));
            }
            else if (i == 1 || i == 5){
                board.setSpot(i, 0, new Knight(true));
                board.setSpot(i, 7, new Knight(false));
            }
            else if (i == 2 || i == 6){
                board.setSpot(i, 0, new Bishop(true));
                board.setSpot(i, 7, new Bishop(false));
            }
            else if (i == 4){
                board.setSpot(i, 0, new Queen(true));
                board.setSpot(i, 7, new Queen(false));
            }
            else {
                board.setSpot(i, 0, new King(true));
                wKingPos = board.getSpot(i, 0);
                board.setSpot(i, 7, new King(false));
                bKingPos = board.getSpot(i, 7);
            }
        }
        drawInitialBoard();
    }
    private Status checkStatus(){
        //TODO Implement method that checks game outcomes
        if (inCheck()) {
            if(noLegalMoves()) return Status.CHECKMATE;
            return Status.CHECK;
        }
        else{
            if(noLegalMoves()) return Status.STALEMATE;
        }
        return Status.ONGOING;
    }

    // Checks if the current player in check
    private boolean inCheck(){
        //Player currentPlayer, oppPlayer;
        ArrayList<Spot> opponentPositions= board.getPlayerPositions(!wTurn);
        Spot curKingPos = wTurn ? wKingPos : bKingPos;
        /*if (wTurn){
            currentPlayer = wPlayer;
            oppPlayer = bPlayer;
            curKingPos = wKingPos;
        }*/
        for (Spot position : opponentPositions){
            Piece curPiece = position.getPiece().get();
            if(curPiece.canMove(board, position, curKingPos)){
                System.out.println("Check");
                return true;
            }
        }
        return false;

    }
    private boolean noLegalMoves(){
        ArrayList<Spot> playerPositions= board.getPlayerPositions(wTurn);
        ArrayList<ArrayList<Move>> allMoves = new ArrayList<>();
        for(Spot pos: playerPositions){
            allMoves.add(getLegalMoves(pos));
        }
        for (ArrayList<Move> moveList : allMoves){
            for(Move curMove : moveList){
                applyMove(curMove);
                if (inCheck()){
                    undoMove(this.board, curMove);
                }
                else {
                    undoMove(this.board, curMove);
                    return false;
                }
            }
        }
        //System.out.println("CHECKMATE");
        return true;
    }

    public ArrayList<Move> getLegalMoves(Spot position){
        if (!position.getPiece().isPresent()) return null;
        ArrayList<Move> moves = new ArrayList<>();
        Piece curPiece = position.getPiece().get();
        for (int i=0; i<8 ; i++) {
            for (int j = 0; j < 8; j++) {
                Spot finish = board.getSpot(i,j);
                if (curPiece.canMove(board, position, board.getSpot(i,j))){
                    moves.add(new Move(position, finish, false));
                }
            }
        }
        return moves;
    }


    public Board getGameBoard() {
        return board;
    }
    /*private void aapplyMove(Move move){
        Spot start = move.getStart();
        Spot finish = move.getFinish();
        Piece pieceMoved = move.getPieceMoved();
        Pane startPane = (Pane) active.get(pieceMoved).getParent();
        Pane endPane = (Pane) getNodeFromGridPane(boardPane, finish.getX(), finish.getY());
        startPane.getChildren().removeAll();
        start.setPiece(null);
        if(finish.getPiece().isPresent() && endPane != null){
            Piece killed = finish.getPiece().get();
            move.setPieceKilled(killed);
            if (wTurn) {
                wKills.add(killed);
                bScore = bScore - killed.getScore();
            }
            else {
                bKills.add(killed);
                wScore = wScore - killed.getScore();
            }

            Platform.runLater(()-> {endPane.getChildren().clear(); });
            kills.put(killed, active.get(killed));
            active.remove(killed);
        }
        board.setSpot(finish.getX(), finish.getY(), pieceMoved);
        if (pieceMoved instanceof King){
            if (pieceMoved.isWhite()) wKingPos = finish;
            else bKingPos = finish;
        }
        Platform.runLater(()-> {endPane.getChildren().add(active.get(pieceMoved));});

    }*/
    public static void applyMove(Board board, Move move){
        Spot start = move.getStart();
        Spot finish = move.getFinish();
        Piece pieceMoved = move.getPieceMoved();
        start.setPiece(null);
        if(finish.getPiece().isPresent()){
            Piece killed = finish.getPiece().get();
            move.setPieceKilled(killed);
        }
        board.setSpot(finish.getX(), finish.getY(), pieceMoved);
    }
    private void applyMove(Move move){
        Piece pieceMoved = move.getPieceMoved();
        applyMove(this.board, move);
        if(move.getPieceKilled() != null){
            Piece killed = move.getPieceKilled();
            if (wTurn) {
                wKills.add(killed);
                bScore = bScore - killed.getScore();
            }
            else {
                bKills.add(killed);
                wScore = wScore - killed.getScore();
            }
            kills.put(killed, active.get(killed));
            active.remove(killed);
        }
        if (pieceMoved instanceof King){
            if (pieceMoved.isWhite()) wKingPos = move.getFinish();
            else bKingPos = move.getFinish();
        }
    }
    public static void undoMove(Board board, Move move){
        Spot start = move.getStart();
        Spot finish = move.getFinish();
        Piece pieceMoved = move.getPieceMoved();
        Piece pieceKilled = move.getPieceKilled();
        board.setSpot(start.getX(), start.getY(), pieceMoved);
        if (pieceKilled != null){
            board.setSpot(finish.getX(), finish.getY(), pieceKilled);
        }
        else{
            board.setSpot(finish.getX(), finish.getY(), null);
        }
        //return board;
    }
    private void undoMove(){
        Move lastMove = movesList.removeLast();
        Piece pieceKilled = lastMove.getPieceKilled();
        undoMove(this.board, lastMove);
        if (pieceKilled != null){
            active.put(pieceKilled, kills.get(pieceKilled));
            kills.remove(pieceKilled);
        }


    }
    private void drawBoard(){
        for (int i=0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Spot boardSpot = board.getSpot(i, j);
                Pane gridSpot = (Pane) getNodeFromGridPane(boardPane, i, j);
                Platform.runLater(()-> {gridSpot.getChildren().clear();});
                if (boardSpot.getPiece().isPresent()){
                    Platform.runLater(()-> {gridSpot.getChildren().add(active.get(boardSpot.getPiece().get()));});
                }
                gridSpot.setBorder(null);
            }
        }
    }
    private void drawInitialBoard(){
        Board board = getGameBoard();
        ArrayList<PieceCanvas> wPieces = new ArrayList<>();
        ArrayList<PieceCanvas> bPieces = new ArrayList<>();
        for (int i=0; i < 8; i++){
            for(int j=0; j<8; j++){
                Spot boardSpot = board.getSpot(i, j);
                Pane gridSpot = (Pane) getNodeFromGridPane(boardPane, i, j);
                if (boardSpot.getPiece().isPresent()) {
                    double x = gridSpot.getWidth();
                    double y = gridSpot.getHeight();
                    Piece curPiece = boardSpot.getPiece().get();
                    //Image icon = new Image(curPiece.getIconURL());
                    PieceCanvas canvas = new PieceCanvas(x, y, curPiece);
                    canvas.widthProperty().bind(gridSpot.widthProperty());
                    canvas.heightProperty().bind(gridSpot.heightProperty());
                    canvas.draw();
                    //GraphicsContext gc = canvas.getGraphicsContext2D();
                    //gc.drawImage(icon, 0, 0, x, y);
                    //canvas.setOnMouseClicked(selectPiece);
                    gridSpot.getChildren().add(canvas);
                    active.put(curPiece, canvas);
                    if (curPiece.isWhite()) wPieces.add(canvas);
                    else bPieces.add(canvas);
                }
                //pane.setOnMouseClicked(selectSpot);
            }
        }
        wPlayer.setPieces(wPieces);
        bPlayer.setPieces(bPieces);
    }
    /*private void redrawBoard(){
        for(int i=0; i<8; i++){
            for(int j=0; i<8; i++){
                Spot boardSpot = board.getSpot(i, j);
                Pane gridSpot = (Pane) getNodeFromGridPane(boardPane, i, j);
                if(boardSpot.getPiece().isPresent()){
                    PieceCanvas pieceCanvas = active.get(boardSpot.getPiece().get());
                    pieceCanvas.setPareb
                }
            }
        }
    }*/
    /*
    private void activatePieces(boolean isWhite){
        for(Piece piece : active.keySet()){
            if (piece.isWhite() == isWhite){
                active.get(piece).setOnMouseClicked(selectPiece);
            }
        }
    }
    private void deactivatePieces(boolean isWhite){
        for(Piece piece : active.keySet()){
            if(piece.isWhite() == isWhite){
                active.get(piece).setOnMouseClicked(null);
            }
        }
    }
    private void activateTiles(boolean isWhite){
        for (int i=0; i < 8; i++){
            for (int j=0; j < 8; j++){
                Spot cur = board.getSpot(i, j);
                Pane pane = (Pane) getNodeFromGridPane(boardPane, i, j);
                if (!cur.getPiece().isPresent()){
                    pane.setOnMouseClicked(selectSpot);
                }
                Piece piece = cur.getPiece().get();
                if (piece.isWhite() != isWhite){
                    pane.setOnMouseClicked(selectSpot);
                }
            }
        }
    }
    private EventHandler<MouseEvent> selectSpot = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (selected == null || !(mouseEvent.getSource() instanceof Pane)) return;
            Pane endPane = (Pane) mouseEvent.getSource();
            Pane startPane = (Pane) selected.getParent();
            int y1 = GridPane.getRowIndex(endPane);
            int x1 = GridPane.getColumnIndex(endPane);
            int y0 = GridPane.getRowIndex(startPane);
            int x0 = GridPane.getColumnIndex(startPane);
            Spot start = board.getSpot(x0, y0);
            Spot end = board.getSpot(x1, y1);
            Move move = new Move(start, end, false);
            if (wTurn) {
                ((HumanPlayer) wPlayer).pushMove(move);
            } else {
                ((HumanPlayer) bPlayer).pushMove(move);
            }
            /*endPane.getChildren().removeAll();
            startPane.getChildren().removeAll();
            endPane.getChildren().add(selected);
            selected = null;
        }
    };
    private EventHandler<MouseEvent> selectPiece = new EventHandler<MouseEvent>(){

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
            event.consume();
        }
    };*/
    protected static Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
    public PieceCanvas getCanvas(Piece piece){
        if (!active.containsKey(piece)) return null;
        return active.get(piece);
    }
    public void play(){
        //TODO Add check for casteling if move possible
        wTurn = true;
        Status gameStatus = Status.ONGOING;
        while (gameStatus == Status.ONGOING || gameStatus == Status.CHECK){
            Move move = wTurn ? wPlayer.getMove() : bPlayer.getMove();
            if (move.getPieceMoved().canMove(board, move.getStart(), move.getFinish())){
                movesList.add(move);
                applyMove(move);
                if(inCheck()){
                    undoMove();
                    continue;
                }
                wTurn = !wTurn;
                gameStatus = checkStatus();
            }
            drawBoard();
        }
    }
}
enum Status{
    ONGOING,CHECK,CHECKMATE,STALEMATE,RESIGNATION
}
