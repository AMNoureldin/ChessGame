package sample;

import game.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.*;

public class Controller {
    @FXML GridPane boardPane;
    private String wKingURL = "file:src/sample/Chess_klt60.png";
    private PieceCanvas selected;
    private Game aGame;

    //TODO Design and implement main menu
    //TODO Find a way to display killed pieces on display
    //TODO Change game start resolution and make the board scale correctly


//    public void testButton(){
//        Pane pane = (Pane) boardPane.getChildren().get(4);
//        double x = pane.getWidth();
//        double y = pane.getHeight();
//        Canvas canvas = new Canvas(x, y);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        Image icon = new Image(wKingURL);
//        gc.drawImage(icon, 0, 0, x, y);
//        pane.getChildren().add(canvas);
//    }
//    public void testButton(){
//        Pane pane = (Pane) boardPane.getChildren().get(4);
//        double x = pane.getWidth();
//        double y = pane.getHeight();
//        Image icon = new Image(wKingURL);
//        PieceCanvas canvas = new PieceCanvas(x, y, icon);
//        canvas.widthProperty().bind(pane.widthProperty());
//        canvas.heightProperty().bind(pane.heightProperty());
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        gc.drawImage(icon, 0, 0, x, y);
//        pane.getChildren().add(canvas);
//    }
    @FXML
    private void drawBoard(){
        Game game = new Game(new HumanPlayer(true, boardPane), new HumanPlayer(false, boardPane), boardPane);
        Runnable runnable = game::play;
        Thread gameThread = new Thread(runnable);
        gameThread.start();
    }


    /*@FXML
    public void drawBoard(){
        if (aGame != null) return;
        aGame = new Game(new HumanPlayer(true), new HumanPlayer(false), boardPane);
        Board board = aGame.getGameBoard();
        for (int i=0; i < 8; i++){
            for(int j=0; j<8; j++){
                Spot cur = board.getSpot(i, j);
                Pane pane = (Pane) getNodeFromGridPane(boardPane, i, j);
                if (cur.getPiece().isPresent()) {
                    double x = pane.getWidth();
                    double y = pane.getHeight();
                    Piece curPiece = cur.getPiece().get();
                    Image icon = new Image(curPiece.getIconURL());
                    PieceCanvas canvas = new PieceCanvas(x, y, icon);
                    canvas.widthProperty().bind(pane.widthProperty());
                    canvas.heightProperty().bind(pane.heightProperty());
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.drawImage(icon, 0, 0, x, y);
                    canvas.setOnMouseClicked(selectPiece);
                    pane.getChildren().add(canvas);
                }
                pane.setOnMouseClicked(selectSpot);

            }
        }
    }
    private EventHandler selectPiece = new EventHandler<MouseEvent>(){

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
        }
    };

    @FXML private EventHandler<MouseEvent> selectSpot = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (selected == null || !(mouseEvent.getSource() instanceof Pane)) return;
            Pane endPane = (Pane) mouseEvent.getSource();
            Pane startPane = (Pane) selected.getParent();
            int y1 = GridPane.getRowIndex(endPane);
            int x1 = GridPane.getColumnIndex(endPane);
            int y0 = GridPane.getRowIndex(startPane);
            int x0 = GridPane.getColumnIndex(startPane);
            Spot start = aGame.getGameBoard().getSpot(x0, y0);
            Spot end = aGame.getGameBoard().getSpot(x1, y1);
            Move move = new Move(start, end, false);

            endPane.getChildren().removeAll();
            startPane.getChildren().removeAll();
            endPane.getChildren().add(selected);
            selected = null;
        }
    };*/
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
}
