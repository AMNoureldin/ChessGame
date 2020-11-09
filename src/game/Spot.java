package game;

import game.piece.Piece;

import java.util.Optional;

public class Spot {
    private int x, y;
    private Optional<Piece> piece;

    public Spot(int x, int y){
        this.x = x;
        this.y = y;
        piece = Optional.empty();
    }

    public void setPiece(Piece piece) {
        this.piece = Optional.ofNullable(piece);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Optional<Piece> getPiece() {
        return piece;
    }
}
