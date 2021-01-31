package game;

import game.piece.Piece;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class HumanPlayer extends Player{

    private volatile Move nextMove;
    private ArrayList<PieceCanvas> ownPieces;
    private Board gameBoard = Board.getBoard();
    private PieceCanvas selected;

    public HumanPlayer(boolean isWhite, GridPane pBoard){
        super(true, isWhite, pBoard);
    }


    @Override
    public void setPieces(ArrayList<PieceCanvas> ownPieces) {
        this.ownPieces = ownPieces;
    }

    private void pushMove(Move nextMove){

        this.nextMove = nextMove;

    }

    public ArrayList<Piece> getPieces() {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (PieceCanvas canvas : ownPieces){
            pieces.add(canvas.getPiece());
        }
        return pieces;
    }

    @Override
    public Move getMove() {
        return null;
    }

}
