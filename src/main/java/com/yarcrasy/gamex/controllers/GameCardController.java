package com.yarcrasy.gamex.controllers;

import com.yarcrasy.gamex.MainView;
import com.yarcrasy.gamex.Models.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GameCardController {

    @FXML
    public Label title;
    public Label stock;

    MainView mainView;
    Game game;

    public GameCardController(MainView ctxt, Game g) {
        mainView = ctxt;

    }

    public void setGame(Game g) {
        game = g;
        if (g == null) return;
        title.setText(g.title);
        stock.setText("stock: " + g.stock);
    }

    @FXML
    public void onAddButtonClicked() {
        System.out.println("Add button clicked for game: " + title.getText());
    }

}
