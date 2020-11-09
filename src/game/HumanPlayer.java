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
        //this.notify();
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
        activatePieces();
        while (nextMove == null){
         }
        deactivatePieces();
        deactivateTiles();
         Move move = nextMove;
         nextMove = null;
         return move;
    }
    private void activatePieces(){
        for(PieceCanvas piece : ownPieces){
            piece.setOnMouseClicked(selectPiece);
        }
    }
    //TODO Remove red border on selected piece
    private void deactivatePieces(){
        for(PieceCanvas piece : ownPieces){
            piece.setOnMouseClicked(null);
        }
    }
    private void deactivateTiles(){
        for (int i=0; i < 8; i++){
            for (int j=0; j < 8; j++){
                Pane pane = (Pane) Game.getNodeFromGridPane(this.getBoard(), i, j);
                pane.setOnMouseClicked(null);
                }
            }
        }

    private void activateTiles(){
        for (int i=0; i < 8; i++){
            for (int j=0; j < 8; j++){
                Spot cur = gameBoard.getSpot(i, j);
                Pane pane = (Pane) Game.getNodeFromGridPane(this.getBoard(), i, j);
                if (!cur.getPiece().isPresent()){
                    pane.setOnMouseClicked(selectSpot);
                }
                else if (cur.getPiece().get().isWhite() != this.isWhite()){
                    pane.setOnMouseClicked(selectSpot);
                }
            }
        }
    }
    private EventHandler<MouseEvent> selectSpot = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (selected == null || !(mouseEvent.getSource() instanceof Pane)) return;
            Pane endPane = (Pane) mouseEvent.getSource();
            Pane startPane = (Pane) selected.getParent();
            int y1 = GridPane.getRowIndex(endPane);
            int x1 = GridPane.getColumnIndex(endPane);
            int y0 = GridPane.getRowIndex(startPane);
            int x0 = GridPane.getColumnIndex(startPane);
            Spot start = gameBoard.getSpot(x0, y0);
            Spot end = gameBoard.getSpot(x1, y1);
            Move move = new Move(start, end, false);
            pushMove(move);
            /*endPane.getChildren().removeAll();
            startPane.getChildren().removeAll();
            endPane.getChildren().add(selected);*/
            selected = null;
        }
    };

    private EventHandler<MouseEvent> selectPiece = new EventHandler<MouseEvent>(){

        public void handle(MouseEvent event){
            if(selected != null) {
                Pane pane = (Pane) selected.getParent();
                pane.setBorder(null);
            }
            PieceCanvas canvas = (PieceCanvas) event.getSource();
            Pane pane = (Pane) canvas.getParent();
            pane.setBorder(new Border(new BorderStroke(Color.DARKRED,
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            selected = canvas;
            event.consume();
            activateTiles();
        }
    };
}
