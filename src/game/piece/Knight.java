package game.piece;

import game.Board;
import game.Spot;

public class Knight extends Piece {

    private final static String wURL = "file:misc/Chess_nlt60.png";
    private final static String bURL = "file:misc/Chess_ndt60.png";
    private final static int SCORE = 3;

    public Knight(boolean isWhite) {
        super(isWhite, SCORE);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        if (!super.canMove(board,start,end)) return false;
        int x1 = start.getX();
        int x2 = end.getX();
        int y1 = start.getY();
        int y2 = end.getY();
        double distance = Math.pow(Math.abs(x1 - x2) ,2) + Math.pow(Math.abs(y1 - y2), 2);
        return (Math.sqrt(distance) == Math.sqrt(5));
    }

    @Override
    public String getIconURL() {
        return isWhite() ? wURL : bURL;
    }
}
