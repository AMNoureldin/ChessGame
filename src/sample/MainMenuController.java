package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class MainMenuController {
    @FXML TextField p1Name;
    @FXML TextField p2Name;
    @FXML Pane gameLogo;
    @FXML Canvas logo;
    private Scene gameScene;
    private Controller gameController;


    private final static String logoURL = "file:misc/Logo.png";
    //TODO Display Logo properly with resizability
    @FXML

    public void setGameController(Controller gameController) {
        this.gameController = gameController;
    }

    public void setGameScene(Scene gameScene) {
        this.gameScene = gameScene;
    }

    @FXML
    private void startGame(ActionEvent actionEvent){
        Stage primaryStage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        gameController.setupGame(p1Name.getText(), p2Name.getText());
        primaryStage.setScene(gameScene);

    }
}
