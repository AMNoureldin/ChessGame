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
    private ArrayList<Piece> wKills;
    private ArrayList<Piece> bKills;
    private int wScore, bScore;
    private int moveCounter;
    private GridPane boardPane;
    private HashMap<Piece, PieceCanvas> active;
    private HashMap<Piece, PieceCanvas> kills;
    private boolean wTurn;
    private Spot wKingPos, bKingPos;
    private LinkedList<Move> movesList;
    private Pawn enPassantPawn;
    //TODO Test all pieces for correct movement
    //TODO Run full game then declare winner.
    public Game(Player wPlayer, Player bPlayer, GridPane boardPane) {
        this.wPlayer = wPlayer;
        this.bPlayer = bPlayer;
        board = Board.getBoard();
        moveCounter = 1;
        wScore = 39;
        bScore = 39;
        wKills = new ArrayList<>();
        bKills = new ArrayList<>();
        movesList = new LinkedList<>();
        this.boardPane = boardPane;
        active = new HashMap<>();
        kills = new HashMap<>();
        wTurn = true;
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
            else if (i == 1 || i == 6){
                board.setSpot(i, 0, new Knight(true));
                board.setSpot(i, 7, new Knight(false));
            }
            else if (i == 2 || i == 5){
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
    public Status checkStatus(){
        //TODO Implement resignation and draw
        if (inCheck()) {
            if (wTurn) ((King)wKingPos.getPiece().get()).setInCheck(true);
            else {
                ((King)bKingPos.getPiece().get()).setInCheck(true);
            }
            if(noLegalMoves()) return Status.CHECKMATE;
            return Status.ONGOING;
        }
        else{
            if(noLegalMoves()) return Status.STALEMATE;
            if (wTurn) ((King)wKingPos.getPiece().get()).setInCheck(false);
            else {
                ((King)bKingPos.getPiece().get()).setInCheck(false);
            }
        }

        return Status.ONGOING;
    }

    // Checks if the current player in check
    private boolean inCheck(){
        return inCheck(this.board, wTurn);

    }
    public static boolean inCheck(Board board, boolean wTurn){
        ArrayList<Spot> opponentPositions= board.getPlayerPositions(!wTurn);
        Spot curKingPos = board.getKingSpot(wTurn);

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
        Board localBoard = board.clone();
        for(Spot pos: playerPositions){
            allMoves.add(getLegalMoves(localBoard, pos.getX(),pos.getY()));
        }
        for (ArrayList<Move> moveList : allMoves){
            for(Move curMove : moveList){
                applyMove(localBoard, curMove);
                if (inCheck(localBoard, wTurn)){
                    undoMove(localBoard, curMove);
                }
                else {
                    undoMove(localBoard, curMove);
                    return false;
                }
            }
        }
        //System.out.println("CHECKMATE");
        return true;
    }
    public static ArrayList<Move> getLegalMoves(Board board, int x, int y){
        Spot position = board.getSpot(x, y);
        if (position == null) return null;
        if (!position.getPiece().isPresent()) return null;
        ArrayList<Move> moves = new ArrayList<>();
        Piece curPiece = position.getPiece().get();
        for (int i=0; i<8 ; i++) {
            for (int j = 0; j < 8; j++) {
                Spot finish = board.getSpot(i,j);
                if (curPiece.canMove(board, position, finish)){
                    moves.add(new Move(position, finish, false));
                }
            }
        }
        return moves;
    }
    private ArrayList<Move> getLegalMoves(Spot position){
        return getLegalMoves(this.board, position.getX(), position.getY());
    }


    public Board getGameBoard() {
        return board;
    }

    public static void applyMove(Board board, Move move){
        Spot start = move.getStart();
        Spot finish = move.getFinish();
        Piece pieceMoved = move.getPieceMoved();
        start.setPiece(null);
        if (move.isCastleMove()){
            applyCastleMove(board, move);
            return; //TODO apply castling
        }
        if(finish.getPiece().isPresent()){
            Piece killed = finish.getPiece().get();
            move.setPieceKilled(killed);
        }
        board.setSpot(finish.getX(), finish.getY(), pieceMoved);
    }
    private static void applyCastleMove(Board board, Move move){
        assert move.getFinish().getPiece().isPresent();
        //Spot start = move.getStart();
        Spot end = move.getFinish();
        if (end.getX() == 0){
            board.setSpot(1, end.getY(), move.getPieceMoved());
            board.setSpot(2, end.getY(), end.getPiece().get());
        }
        else if (end.getX() == 7){
            board.setSpot(5, end.getY(), move.getPieceMoved());
            board.setSpot(4, end.getY(), end.getPiece().get());
        }
        end.setPiece(null);
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
            //active.remove(killed);
        }
        if (pieceMoved instanceof King){
            if (pieceMoved.isWhite()) wKingPos = board.getKingSpot(true);
            else bKingPos = board.getKingSpot(false);
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
            //active.put(pieceKilled, kills.get(pieceKilled));
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

    public ArrayList<Piece> getwKills() {
        return wKills;
    }

    public ArrayList<Piece> getbKills() {
        return bKills;
    }

    public int getScore(boolean wTurn){
        return wTurn ? wScore: bScore;
    }

    public Player getCurrentPlayer(){
        return wTurn ? wPlayer : bPlayer;
    }
    public boolean validateMove(Move move){
        if (move.getPieceMoved().canMove(board, move.getStart(), move.getFinish())){
            if (move.getFinish().getPiece().isPresent() && move.getPieceMoved() instanceof King){
                if (move.getFinish().getPiece().get() instanceof Rook){
                    move.setCastleMove();
                }
            }

            movesList.add(move);
            applyMove(move);
            if(inCheck()){
                undoMove();
                return false;
            }
            wTurn = !wTurn;
            if(move.getPieceMoved() instanceof SpecialPiece){
                SpecialPiece pieceMoved = (SpecialPiece) move.getPieceMoved();
                pieceMoved.setHasMoved(true);
                if(pieceMoved  instanceof Pawn){
                    Pawn pawnMoved = (Pawn) pieceMoved;
                    if (!pawnMoved.hasMoved()){
                        pawnMoved.setEnPassant(true);
                        if(enPassantPawn != null){
                            enPassantPawn.setEnPassant(false);
                        }
                        enPassantPawn = pawnMoved;
                    }
                    pawnMoved.setHasMoved(true);
                    return true;
                }
            }
            if(enPassantPawn != null){
                enPassantPawn.setEnPassant(false);
                enPassantPawn = null;
            }
            //gameStatus = checkStatus();
            return true;
        }
        return false;
    }

}
