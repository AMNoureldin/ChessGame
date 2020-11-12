package game.piece;

import game.Board;
import game.Spot;

public class King extends Piece {
    private boolean hasMoved;
    private boolean inCheck;
    private final static String wKingURL = "file:misc/Chess_klt60.png";
    private final static String bKingURL = "file:misc/Chess_kdt60.png";
    private final static int SCORE = -1;
    public King(boolean isWhite){
        super(isWhite, SCORE);
        this.hasMoved = false;
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end){
        if (!super.canMove(board,start,end)) return false;
        int endX = end.getX();
        int x = Math.abs(start.getX() - endX);
        int endY = end.getY();
        int y = Math.abs(start.getY() - endY);
        int distance = x^2 + y^2;
        if (distance > 1) return false;
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

    @Override
    public String getIconURL(){
        return isWhite() ? wKingURL : bKingURL;
    }
}
