package game;

import game.piece.Piece;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

//TODO Implement getMove()
public class ComputerPlayer extends Player{

    public ComputerPlayer(boolean isWhite, GridPane gridPane){
        super(false, isWhite, gridPane);
    }

    @Override
    public void setPieces(ArrayList<PieceCanvas> ownPieces) {

    }

    @Override
    public ArrayList<Piece> getPieces() {
        return null;
    }


    @Override
    public Move getMove() {
        return null;
    }
}
