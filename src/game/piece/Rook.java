package game.piece;

import game.Board;
import game.Spot;

public class Rook extends SpecialPiece {

    private final static String wURL = "file:misc/Chess_rlt60.png";
    private final static String bURL = "file:misc/Chess_rdt60.png";
    private final static int SCORE = 5;

    public Rook(boolean isWhite) {
        super(isWhite, SCORE);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        if (!super.canMove(board,start,end)) return false;
        int x = start.getX() - end.getX();
        int y = start.getY() - end.getY();
        if(x==0 || y==0) return clearWay(board, start, end);
        return false;
    }
    @Override
    public String getIconURL() {
        return isWhite() ? wURL : bURL;
    }
}
