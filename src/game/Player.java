package game;

import game.piece.King;
import game.piece.Piece;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public abstract class Player {
    private boolean isWhite;
    private boolean isHuman;
    private GridPane aBoard;
    private King aKing;

     Player(boolean isHuman, boolean isWhite, GridPane pBoard){
        this.isHuman = isHuman;
        this.isWhite = isWhite;
        aBoard = pBoard;
    }
    public abstract void setPieces(ArrayList<PieceCanvas> ownPieces);
    public abstract ArrayList<Piece> getPieces();
    public King getKing(){
        return aKing;
    }
     protected GridPane getBoard(){
         return aBoard;
     }

     public boolean isWhite() {
        return isWhite;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public abstract Move getMove();

}

