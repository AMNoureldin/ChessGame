package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChessGame extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("mainwindow.fxml"));
        primaryStage.setTitle("Chess");

        FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("mainwindow.fxml"));
        Parent mainMenu  = mainMenuLoader.load();
        Scene mainScene = new Scene(mainMenu, 755,755);

        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("gamewindow.fxml"));
        Parent gameRoot  = gameLoader.load();
        Scene gameScene = new Scene(gameRoot, 755,755);
        MainMenuController menuController = (MainMenuController) mainMenuLoader.getController();
        menuController.setGameScene(gameScene);
        menuController.setGameController(gameLoader.getController());
        primaryStage.setScene(mainScene);
        primaryStage.show();
        //menuController.drawLogo();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
