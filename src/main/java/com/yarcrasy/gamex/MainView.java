package com.yarcrasy.gamex;

import com.yarcrasy.gamex.Models.Game;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

public class MainView extends Application {

    @FXML
    private ScrollPane gameList;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("GameX");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        MainView controller = fxmlLoader.getController();
        controller.loadGames();
    }

    public void loadGames() {
        List<Game> gl = DBConnector.instance.getAllGames();
        VBox content = new VBox();
        content.spacingProperty().setValue(1);

        for (Game g : gl) {
            GameCardController gcc = new GameCardController();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GameCard.fxml"));
                fxmlLoader.setController(gcc);
                Node gameCard = fxmlLoader.load();
                gcc.setGame(g);
                content.getChildren().add(gameCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        gameList.setContent(content);
    }

    @FXML
    public void onGameSearchBtnClick(ActionEvent e) {
        System.out.println("Game Search Button Clicked");
    }

}