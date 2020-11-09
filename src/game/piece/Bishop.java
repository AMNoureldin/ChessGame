package game.piece;

import game.Board;
import game.Spot;

public class Bishop extends Piece {

    private final static String wURL = "file:misc/Chess_blt60.png";
    private final static String bURL = "file:misc/Chess_bdt60.png";
    private final static int SCORE = 3;

    public Bishop(boolean isWhite){
        super(isWhite, SCORE);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end){
        if (!super.canMove(board,start,end)) return false;
        int x1 = start.getX();
        int x2 = end.getX();
        int y1 = start.getY();
        int y2 = end.getY();
        if(Math.abs(y1-y2) == Math.abs(x1 - x2)){
            return clearWay(board, start, end);
        }
        return false;
    }

    @Override
    public String getIconURL() {
        return isWhite() ? wURL : bURL;
    }
}
