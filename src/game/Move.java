package game;

import game.piece.Piece;

import java.util.Optional;

public class Move {
    private Spot start, finish;
    private Piece pieceMoved;
    private Piece pieceKilled;
    private boolean isCastleMove;



    public Move(Spot start, Spot finish, boolean isCastleMove) {
        assert start.getPiece().isPresent();
        this.start = start;
        this.finish = finish;
        this.pieceMoved = start.getPiece().get();
        this.isCastleMove = isCastleMove;
    }

    public Spot getStart() {
        return start;
    }
    public Piece getPieceKilled() {
        return pieceKilled;
    }
    public void setPieceKilled(Piece pieceKilled) {
        this.pieceKilled = pieceKilled;
    }

    public Spot getFinish() {
        return finish;
    }

    public Piece getPieceMoved() {
        return pieceMoved;
    }

    public void setCastleMove() { isCastleMove = true;
    }

    public boolean isCastleMove() {
        return isCastleMove;
    }
}
