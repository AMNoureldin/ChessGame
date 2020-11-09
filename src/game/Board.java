package game;

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
    public Spot getKingSpot(){
        return null; //TODO Implement get KingSpot as solution to updating the kings positons in game
    }
    public Spot getSpot(int x, int y){
        return boxes[x][y];
    }

    void setSpot(int x, int y, Piece piece){
        Spot spot = getSpot(x, y);
        spot.setPiece(piece);
    }

    void reset(){
        board = new Board();
    }
}
