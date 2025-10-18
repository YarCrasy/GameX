package com.yarcrasy.gamex;

import com.yarcrasy.gamex.Models.Game;
import com.yarcrasy.gamex.controllers.GameCardController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

public class MainView extends Application {

    @FXML
    private ScrollPane gameList;
    @FXML
    private TextField titleField;
    @FXML
    public ScrollPane cartList;

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

    void updateGameList(List<Game> gl) {
        VBox content = new VBox();
        content.spacingProperty().setValue(1);

        for (Game g : gl) {
            GameCardController gcc = new GameCardController(this, g);
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

    public void loadGames() {
        List<Game> gl = DBConnector.instance.getAllGames();
        updateGameList(gl);
    }

    @FXML
    public void onGameSearchBtnClick(ActionEvent e) {
        List<Game> gl = DBConnector.instance.getGamesByTitle(titleField.getText());
        updateGameList(gl);
    }

    public void addGameToCart(Game g) {

    }

}