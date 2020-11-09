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
        int x = Math.abs(start.getX() - end.getX());
        int y = Math.abs(start.getY() - end.getY());
        int distance = x^2 + y^2;
        return (distance == 1) && clearWay(board, start, end);
    }

    @Override
    public String getIconURL(){
        return isWhite() ? wKingURL : bKingURL;
    }
}
