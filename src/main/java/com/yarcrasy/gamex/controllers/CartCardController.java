package com.yarcrasy.gamex.controllers;

import com.yarcrasy.gamex.MainView;
import com.yarcrasy.gamex.Models.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CartCardController{

    @FXML
    public Label title;
    public Label amount;

    int quantity = 0;

    MainView mainView;
    Game game;
    public CartCardController(MainView ctxt, Game g) {
        mainView = ctxt;
        game = g;
    }

    public void setCartCard(Game g) {
        game = g;
        if (g == null) return;
        title.setText(g.title);
        quantity++;
        amount.setText(quantity + "");
    }
}
