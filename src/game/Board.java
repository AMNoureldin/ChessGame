package game;

import game.piece.King;
import game.piece.Piece;

import java.util.ArrayList;
import java.util.Optional;

public class Board {
    private Spot[][] boxes;
    private static Board board = new Board();

    private Board(){
         boxes = new Spot[8][8];
        for(int i=0; i < 8; i++){
            for(int j=0; j < 8; j++){
                boxes[i][j] = new Spot(i, j);
            }
        }
    }

    public static Board getBoard(){
        return board;
    }
    //TODO add error checking getSpot
    public ArrayList<Spot> getPlayerPositions(boolean isWhite){
        ArrayList<Spot> positions = new ArrayList<>();
        for (int i=0; i<8 ; i++){
            for(int j=0; j<8; j++){
                Optional<Piece> piece = boxes[i][j].getPiece();
                if (piece.isPresent()){
                    if(piece.get().isWhite() == isWhite){
                        positions.add(boxes[i][j]);
                    }
                }
            }
        }
        return positions;
    }
    public Spot getKingSpot(boolean isWhite){
        for (int i=0; i<8 ; i++){
            for(int j=0; j<8; j++){
                Optional<Piece> piece = boxes[i][j].getPiece();
                if (piece.isPresent()){
                    if(piece.get().isWhite() == isWhite && piece.get() instanceof King){
                        return boxes[i][j];
                    }
                }
            }
        }
        return null;
    }
    public Spot getSpot(int x, int y){
        if (x > 7 || x < 0 || y > 7 || y < 0) return null;

        return boxes[x][y];
    }

    void setSpot(int x, int y, Piece piece){
        Spot spot = getSpot(x, y);
        if (spot == null) return;
        spot.setPiece(piece);
    }

    void reset(){
        board = new Board();
    }
    public Board clone(){
        Board newBoard = new Board();
        for(int i=0; i < 8; i++){
            for(int j=0; j < 8; j++){
                Optional<Piece> piece = this.boxes[i][j].getPiece();
                if(piece.isPresent()){
                    newBoard.boxes[i][j].setPiece(piece.get());
                }
            }
        }
        return newBoard;
    }
}
