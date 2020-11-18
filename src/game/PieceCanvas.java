package game;

import game.piece.Piece;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


public class PieceCanvas extends Canvas {
    private Image aImg;
    private Piece aPiece;


    public PieceCanvas(double x, double y, Piece pPiece){
        super(x, y);
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
        aPiece = pPiece;
        aImg = new Image(aPiece.getIconURL());
    }

     public void draw() {
        double width = getWidth();
        double height = getHeight();
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.drawImage(aImg, 0, 0, width, height);
    }

    public Piece getPiece() {
        return aPiece;
    }

    public boolean isWhite(){
        return aPiece.isWhite();
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
}
