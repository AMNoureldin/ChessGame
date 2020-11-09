package game.piece;

import game.Board;
import game.Spot;

public class Queen extends Piece {

    private final static String wURL = "file:misc/Chess_qlt60.png";
    private final static String bURL = "file:misc/Chess_qdt60.png";
    private final static int SCORE = 9;

    public Queen(boolean isWhite){
        super(isWhite, SCORE);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end){
        if (!super.canMove(board,start,end)) return false;
        return clearWay(board, start, end);
    }

    @Override
    public String getIconURL() {
        return isWhite() ? wURL : bURL;
    }
}
