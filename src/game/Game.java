package game;

import game.piece.*;
import game.piece.Piece;
import javafx.scene.Node;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class Game {
    private Player wPlayer, bPlayer;
    private Board board;
    private int wScore, bScore;
    private int moveCounter;
    private ArrayList<Piece> active;
    private ArrayList<Piece> kills;
    private boolean wTurn;
    private Spot wKingPos, bKingPos;
    private LinkedList<Move> movesList;
    private Pawn enPassantPawn;
    // Game model constructor
    public Game(Player wPlayer, Player bPlayer, GridPane boardPane) {
        this.wPlayer = wPlayer;
        this.bPlayer = bPlayer;
        board = Board.getBoard();
        moveCounter = 1;
        wScore = 39;
        bScore = 39;
        movesList = new LinkedList<>();
        active = new ArrayList<>();
        kills = new ArrayList<>();
        wTurn = true;
        initStd();
    }
    // Initialize board for standard setup by creating the pieces and setting the board
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
        initActive();
    }
    // Intialize arraylist "active" with the pieces in play.
    private void initActive(){
        for (int i=0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Spot boardSpot = board.getSpot(i, j);
                if (boardSpot.getPiece().isPresent()) {
                    Piece curPiece = boardSpot.getPiece().get();
                    active.add(curPiece);
                }
            }
        }
    }
    // Checks the status of the game and returns a Status enum value.
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

    // Checks if the player of the given color is in check on the given board.
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
    // Checks if the player whose turn it is has no legal moves available.
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
    // Returns a list of all the available moves for a piece at board position x,y
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
    // Returns the game board of this game instance.
    public Board getGameBoard() {
        return board;
    }
    // Applies a move to a given game board.
    public static void applyMove(Board board, Move move){
        Spot start = move.getStart();
        Spot finish = move.getFinish();
        Piece pieceMoved = move.getPieceMoved();
        start.setPiece(null);
        if (move.isCastleMove()){
            applyCastleMove(board, move);
            return;
        }
        if(finish.getPiece().isPresent()){
            Piece killed = finish.getPiece().get();
            move.setPieceKilled(killed);
        }
        board.setSpot(finish.getX(), finish.getY(), pieceMoved);
    }
    // Applies promotion move to given game board.
    private static void applyPromotion(Board board, Move move){
        assert move.isPromotion();
        Piece promotedTo = move.getPromotedTo();
        move.getStart().setPiece(null);
        board.setSpot(move.getFinish().getX(), move.getFinish().getY(), promotedTo);
    }
    // Applies castling move to given game board.
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
    // Instance method for applying a move to the board of a game instance.
    private void applyMove(Move move){
        Piece pieceMoved = move.getPieceMoved();
        if (move.isPromotion()){
            applyPromotion(this.board, move);
            active.add(move.getPromotedTo());
            if(wTurn) wScore = wScore + move.getPromotedTo().getScore() - 1;
            else bScore = bScore + move.getPromotedTo().getScore() - 1;
        }
        else applyMove(this.board, move);
        if(move.getPieceKilled() != null){
            Piece killed = move.getPieceKilled();
            if (wTurn) {
                bScore = bScore - killed.getScore();
            }
            else {
                wScore = wScore - killed.getScore();
            }
            kills.add(killed);
            //active.remove(killed);
        }
        if (pieceMoved instanceof King){
            if (pieceMoved.isWhite()) wKingPos = board.getKingSpot(true);
            else bKingPos = board.getKingSpot(false);
        }
    }
    // Reverses given move on given game board.
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
    // Reverses last move played
    private void undoMove(){ //TODO Check undoing a castling and promotion
        Move lastMove = movesList.removeLast();
        Piece pieceKilled = lastMove.getPieceKilled();
        undoMove(this.board, lastMove);
        if (pieceKilled != null){
            kills.remove(pieceKilled);
            if (wTurn) {
                bScore = bScore + pieceKilled.getScore();
            }
            else {
                wScore = wScore + pieceKilled.getScore();
            }
        }
        if (lastMove.getPieceMoved() instanceof King){
            if (lastMove.getPieceMoved().isWhite()) wKingPos = board.getKingSpot(true);
            else bKingPos = board.getKingSpot(false);
        }

    }



    // Returns the list of active pieces of the player whose turn it is.
    public ArrayList<Piece> getCurrentPlayerPieces(){
        ArrayList<Piece> curPieces = new ArrayList<>();
        for(Piece piece : active){
            if (piece.isWhite() == wTurn){
                curPieces.add(piece);
            }
        }
        return curPieces;
    }


    // Returns the killed pieces of a player based on the isWhite color parameter
    public ArrayList<Piece> getPlayerKills(boolean isWhite){
        ArrayList<Piece> pieces = new ArrayList<>();
        for (Piece piece: kills){
            if (piece.isWhite() == isWhite){
                pieces.add(piece);
            }
        }
        return pieces;
    }
    // Returns the score of selected player.
    public int getScore(boolean wTurn){
        return wTurn ? wScore: bScore;
    }
    // Returns the player instance whose turn it is.
    public Player getCurrentPlayer(){
        return wTurn ? wPlayer : bPlayer;
    }
    // Checks that a move is legal then applies it to the game instance and advances turn.
    public boolean validateMove(Move move){
        if (move.getPieceMoved().isWhite() != wTurn) return false;
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
