package game.piece;

public abstract class SpecialPiece extends Piece {
    private boolean hasMoved;

    public SpecialPiece(boolean isWhite, int score) {
        super(isWhite, score);
        this.hasMoved = false;
    }

    public final boolean hasMoved() {
        return hasMoved;
    }

    public final void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}
