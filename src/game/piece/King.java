package game.piece;

import game.Board;
import game.Spot;

import java.util.ArrayList;

public class King extends SpecialPiece {
    private boolean inCheck;
    private final static String wKingURL = "file:misc/Chess_klt60.png";
    private final static String bKingURL = "file:misc/Chess_kdt60.png";
    private final static int SCORE = -1;
    public King(boolean isWhite){
        super(isWhite, SCORE);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end){
        if (!super.canMove(board,start,end)) return false;
        int endX = end.getX();
        int x = Math.abs(start.getX() - endX);
        int endY = end.getY();
        int y = Math.abs(start.getY() - endY);
        int distance = x^2 + y^2;
        if ((x == 4 || x == 3) && y == 0){
            if (end.getPiece().isPresent()){
                return canCastle(board, start, end);
            }
        }
        else if (distance > 1) return false;
        for(int i = -1; i < 2; i++){
            for (int j = -1; j<2; j++){
                Spot nextSpot = board.getSpot(endX + i, endY + j);

                if (nextSpot != null){
                    if(nextSpot.getPiece().isPresent()){
                        Piece piece = nextSpot.getPiece().get();
                        if (piece instanceof King && piece.isWhite() != isWhite()) return false;
                    }
                }

            }
        }
        return clearWay(board, start, end);
    }

    public void setInCheck(boolean inCheck) {
        this.inCheck = inCheck;
    }

    private boolean canCastle(Board board, Spot start, Spot end){
        if (!(end.getPiece().get() instanceof Rook)) return false;
        Rook target = (Rook) end.getPiece().get();
        if (hasMoved() || target.hasMoved()) return false;
        if (!clearWay(board, start, end)) return false;
        if (inCheck) return false;
        ArrayList<Spot> opponentPositions = board.getPlayerPositions(!isWhite());
        Spot crossing;
        if (end.getX() == 0){
            crossing = board.getSpot(start.getX() - 2, end.getY());
        }
        else if (end.getX() == 7){
            crossing = board.getSpot(start.getX() + 2, end.getY());
        }
        else {
            return false;
        }
        for (Spot pos : opponentPositions){
            if (pos.getPiece().isPresent()){
                if (pos.getPiece().get().canMove(board, pos, crossing)) return false;
            }
        }
        return true;
    }

    @Override
    public String getIconURL(){
        return isWhite() ? wKingURL : bKingURL;
    }
}
