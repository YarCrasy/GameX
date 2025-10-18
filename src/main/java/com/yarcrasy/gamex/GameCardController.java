package com.yarcrasy.gamex;

import com.yarcrasy.gamex.Models.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameCardController {

    @FXML
    public Label title;

    @FXML
    public Label stock;

    public void setGame(Game g) {
        if (g == null) return;
        title.setText(g.title);
        stock.setText("stock: " + String.valueOf(g.stock));
    }
}
