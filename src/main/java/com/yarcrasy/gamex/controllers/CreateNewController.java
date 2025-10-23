package com.yarcrasy.gamex.controllers;

import com.yarcrasy.gamex.DBConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateNewController {

    public GridPane gamePane;
    public GridPane clientPane;
    public VBox parentView;

    // Campos juego
    public TextField gTitleField;
    public TextField gPlatformField;
    public TextField gPriceField;
    public TextField gGenreField;
    public TextField gStockField;

    // Campos cliente
    public TextField cDniField;
    public TextField cFullNameField;
    public TextField cEmailField;
    public TextField cAddressField;
    public Button submitBtn;


    ViewType viewType = ViewType.GAME;

    public void setType(ViewType viewType) {
        this.viewType = viewType;
        updateViewForMode();
    }

    private void updateViewForMode() {
        if (viewType == ViewType.GAME) parentView.getChildren().remove(clientPane);
        else parentView.getChildren().remove(gamePane);
    }

    @FXML
    public void onSubmit(ActionEvent e) {
        boolean ok = false;
        if (viewType == ViewType.CLIENT) {
            String dni = cDniField.getText();
            String name = cFullNameField.getText();
            String email = cEmailField.getText();
            String address = cAddressField.getText();
            if (dni == null || dni.trim().isEmpty() || name == null || name.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Campos obligatorios", "DNI y Nombre son obligatorios");
                return;
            }
            ok = DBConnector.instance.addClient(dni.trim(), name.trim(), email == null ? "" : email.trim(), address == null ? "" : address.trim());
        }
        else {
            String title = gTitleField.getText();
            String platform = gPlatformField.getText();
            double price = 0.0;
            try { price = Double.parseDouble(gPriceField.getText()); } catch (Exception ex) { }
            String genre = gGenreField.getText();
            int stock = 0;
            try { stock = Integer.parseInt(gStockField.getText()); } catch (Exception ex) { }
            if (title == null || title.trim().isEmpty() || platform == null || platform.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Campos obligatorios", "Título y Plataforma son obligatorios");
                return;
            }
            ok = DBConnector.instance.addGame(title.trim(), platform.trim(), price, genre == null ? "" : genre.trim(), stock);
        }

        if (ok) {
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Registro creado correctamente");
            Node src = (Node) e.getSource();
            Stage st = (Stage) src.getScene().getWindow();
            st.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo crear el registro");
        }
    }

    private void showAlert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}