package game.piece;

import game.Board;
import game.Spot;

import java.util.Optional;

public abstract class Piece {

    private boolean isWhite;
    private boolean isKilled;
    private int score;


    Piece(boolean isWhite, int score){
        this.isWhite = isWhite;
        this.isKilled = false;
        this.score = score;
    }

    public boolean isKilled() {
        return isKilled;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setKilled(boolean killed) {
        isKilled = killed;
    }

    public int getScore() {
        return score;
    }

    final boolean clearWay(Board board, Spot start, Spot end){
        int x1 = start.getX();
        int x2 = end.getX();
        int y1 = start.getY();
        int y2 = end.getY();
        if (x1 - x2 == 0){
            int stIndex = Math.min(y1, y2);
            int endIndex = Math.max(y1, y2);
            for(int i=stIndex+1; i < endIndex; i++){
                Spot cur = board.getSpot(x1, i);
                Optional<Piece> piece = cur.getPiece();
                if(piece.isPresent()) return false;
            }
            return true;
        }
        else if(y1 - y2 == 0){
            int stIndex = Math.min(x1, x2);
            int endIndex = Math.max(x1, x2);
            for(int i=stIndex+1; i < endIndex; i++){
                Spot cur = board.getSpot(i, y1);
                Optional<Piece> piece = cur.getPiece();
                if(piece.isPresent()) return false;
            }
            return true;
        }
        else if(Math.abs(y1-y2) == Math.abs(x1 - x2)) {
            int stx = Math.min(x1, x2);
            int sty = stx == x1 ? y1 : y2;
            int endx = Math.max(x1, x2);
            int endy = sty == y1 ? y2 : y1;
            if (sty <= endy) {
                for (int i = stx + 1, j = sty + 1; i < endx && j < endy; i++, j++) {
                    Spot cur = board.getSpot(i, j);
                    Optional<Piece> piece = cur.getPiece();
                    if (piece.isPresent()) return false;
                }
                return true;
            } else {
                for (int i = stx + 1, j = sty - 1; i < endx && j > endy; i++, j--) {
                    Spot cur = board.getSpot(i, j);
                    Optional<Piece> piece = cur.getPiece();
                    if (piece.isPresent()) return false;
                }
                return true;
            }
        }
        else{
            return false;
        }
    }

    public boolean canMove(Board board, Spot start, Spot end){
        if (start == end) return false;
        if (end.getPiece().isPresent()){
            //if (end.getPiece().get().isWhite() == isWhite()) return false;
        }
        return true;
    }


    public abstract String getIconURL();

}
