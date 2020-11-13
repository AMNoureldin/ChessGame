package game.piece;

import game.Board;
import game.Spot;

public class Pawn extends SpecialPiece {

    //private boolean hasMoved;
    private boolean enPassant;
    private final static String wURL = "file:misc/Chess_plt60.png";
    private final static String bURL = "file:misc/Chess_pdt60.png";
    private final static int SCORE = 1;



    public Pawn(boolean isWhite) {
        super(isWhite, SCORE);
        //hasMoved = false;
        enPassant = false;
    }

    public void setEnPassant(boolean enPassant) {
        this.enPassant = enPassant;
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        if (!super.canMove(board,start,end)) return false;
        int x = Math.abs(end.getX() - start.getX());
        int y = end.getY() - start.getY();
        y = isWhite() ? y : (-1 * y);
        if (x > 1 || y > 2 || y < 0) return false;

        if (x==0) {
            if(end.getPiece().isPresent()) return false;
            if(y == 2 && !hasMoved()) {
                boolean clear = clearWay(board, start, end);
                //hasMoved = clear;
                return clear;
            }
            return y == 1;
        }
        else if(x == 1 && y != 2){
            if(!end.getPiece().isPresent()) return false;
            if(y==0){
                Piece killPiece = end.getPiece().get();
                if(killPiece instanceof Pawn){
                    return ((Pawn) killPiece).isEnPassant();
                }
            }
            return y==1;
        }
        return false;
    }

    private boolean isEnPassant() {
        return enPassant;
    }

    @Override
    public String getIconURL() {
        return isWhite() ? wURL : bURL;
    }
}
