package com.yarcrasy.gamex.controllers;

import com.yarcrasy.gamex.Models.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CartCardController{

    @FXML
    public Label title;
    public Label amount;

    public int quantity = 0;

    public Game game;
    public CartCardController(Game g) {
        game = g;
    }

    public void setCartCard(Game g) {
        game = g;
        if (g == null) return;
        title.setText(g.title);
        quantity++;
        amount.setText(quantity + "");
    }

    public void incrementQuantity() {
        quantity++;
        amount.setText(quantity + "");
    }

   public void decrementQuantity() {
       if (quantity > 0) {
           quantity--;
           amount.setText(String.valueOf(quantity));
           if (quantity <= 0) {
               removeThisCardFromParent();
           }
       }
   }

   private void removeThisCardFromParent() {
       javafx.application.Platform.runLater(() -> {
           javafx.scene.Node node = title != null ? title.getParent() : null;
           if (node == null) return;
           javafx.scene.Parent parent = node.getParent();
           if (parent instanceof javafx.scene.layout.Pane) {
               ((javafx.scene.layout.Pane) parent).getChildren().remove(node);
           }
       });
   }

}
