package com.yarcrasy.gamex;

import com.yarcrasy.gamex.Models.*;
import com.yarcrasy.gamex.controllers.*;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.*;

public class MainView extends Application {

    public static MainView instance;

    public GridPane clientInfoPane;
    public ScrollPane gameList;
    public TextField titleField;
    public ScrollPane addedGameList;

    public TextField clientField;
    public Label clientIdLabel;
    public Label clientNameLabel;
    public Label clientAddressLabel;
    public Label clientDniLabel;
    public Label clientEmailLabel;
    public CheckBox clientFrequentCheckBox;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("GameX");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        instance = fxmlLoader.getController();
        instance.loadGames();
    }

    @FXML
    public void initialize() {
        if (clientField != null) {
            new ClientSearchBox(clientField, this::populateClientDetails);
        }
    }

    private void populateClientDetails(Client c) {
        if (c == null) {
            clearClientDetails();
            return;
        }
        clientIdLabel.setText(c.id == null ? "ID: " : "ID: " + c.id);
        clientNameLabel.setText(c.fullName == null ? "Nombre: " : "Nombre: " + c.fullName);
        clientAddressLabel.setText(c.address == null ? "Dirección: " : "Dirección: " + c.address);
        clientDniLabel.setText(c.dni == null ? "DNI: " : "DNI: " +  c.dni);
        clientEmailLabel.setText(c.email == null ? "Email: " : "Email: " + c.email);
        clientFrequentCheckBox.setSelected(c.isFrequent);
    }

    private void clearClientDetails() {
        if (clientIdLabel != null) clientIdLabel.setText("ID: ");
        if (clientNameLabel != null) clientNameLabel.setText("Nombre: ");
        if (clientAddressLabel != null) clientAddressLabel.setText("Dirección: ");
        if (clientDniLabel != null) clientDniLabel.setText("DNI: ");
        if (clientEmailLabel != null) clientEmailLabel.setText("Email: ");
        if (clientFrequentCheckBox != null) clientFrequentCheckBox.setSelected(false);
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
                gcc.setGameCard(g);
                content.getChildren().add(gameCard);
            } catch (IOException e) {
                System.err.println("Error al cargar la tarjeta de juego: " + e.getMessage());
            }
        }
        gameList.setContent(content);
    }

    public void loadGames() {
        List<Game> gl = DBConnector.instance.getAllGames();
        updateGameList(gl);
    }

    public void onGameSearchBtnClick(ActionEvent ignoredE) {
        List<Game> gl = DBConnector.instance.getGamesByTitle(titleField.getText());
        updateGameList(gl);
    }

    public void onNewGameMenu(ActionEvent ignoredE) {
        openCreateNewWindow(ViewType.GAME);
    }

    public void onNewClientMenu(ActionEvent ignoredE) {
        openCreateNewWindow(ViewType.CLIENT);
    }

    public void onListGamesMenu(ActionEvent ignoredE) {
        openDisplayListWindow(ViewType.GAME);
    }

    public void onListClientsMenu(ActionEvent ignoredE) {
        openDisplayListWindow(ViewType.CLIENT);
    }

    public void onRentalsHistoryMenu(ActionEvent ignoredE) {
        openDisplayListWindow(ViewType.RENTAL);
    }

    private void openCreateNewWindow(ViewType type) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateNew.fxml"));
            Parent root = loader.load();
            com.yarcrasy.gamex.controllers.CreateNewController ctrl = loader.getController();
            ctrl.setType(type);

            Stage stage = new Stage();
            stage.setTitle(type == ViewType.GAME ? "Crear nuevo juego" : "Crear nuevo cliente");
            stage.setScene(new Scene(root));
            stage.initOwner(titleField.getScene().getWindow());
            stage.show();
        } catch (IOException ex) {
            System.err.println("Error al abrir la ventana de creación: " + ex.getMessage());
        }
    }

    private void openDisplayListWindow(ViewType type) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DisplayList.fxml"));
            Parent root = loader.load();
            DisplayListController ctrl = loader.getController();
            ctrl.setType(type);

            Stage stage = new Stage();
            stage.setTitle(type == ViewType.GAME ? "Lista de juegos" :
                    type == ViewType.CLIENT ? "Lista de clientes" :
                            "Historial de alquileres");
            stage.setScene(new Scene(root));
            stage.initOwner(titleField.getScene().getWindow());
            stage.show();
        } catch (IOException ex) {
            System.err.println("Error al abrir la ventana de lista: " + ex.getMessage());
        }
    }

    public void addGameToCart(Game g) {
        VBox content;
        Node existing = addedGameList.getContent();

        if (existing instanceof VBox) {
            content = (VBox) existing;
        } else if (existing instanceof Pane pane) {
            content = new VBox();
            content.getChildren().addAll(pane.getChildren());
        } else {
            content = new VBox();
        }

        for (Node child : content.getChildren()) {
            Object ud = child.getUserData();
            if (ud instanceof CartCardController existingCtrl) {
                Game existingGame = existingCtrl.game;
                if (existingGame != null && Objects.equals(existingGame.id, g.id)) {
                    existingCtrl.incrementQuantity();
                    addedGameList.setContent(content);
                    return;
                }
            }
        }

        addedGameList.setContent(content);
        content.setSpacing(1);
        CartCardController ccc = new CartCardController(g);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CartCard.fxml"));
            fxmlLoader.setController(ccc);
            Node gameCard = fxmlLoader.load();
            ccc.setCartCard(g);
            gameCard.setUserData(ccc);
            content.getChildren().add(gameCard);
        } catch (IOException e) {
            System.err.println("Error al añadir juego al carrito: " + e.getMessage());
        }
    }

    public void rstDataBase() {
        DBConnector.instance.rstDataBase();
        loadGames();
        clearCart();
    }

    public void clearCart() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que deseas vaciar el carrito?\nEsta acción no se puede deshacer.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                clearClientDetails();
                addedGameList.setContent(new VBox());
                loadGames();
            }
        });
    }

    public void processRental() {
        String dniText = clientIdLabel.getText().replace("ID: ", "");
        System.out.println("dniText: -" + dniText + "-");
        if (dniText.isEmpty()) {
            rentalFailureAlert("Debe seleccionar un cliente para procesar el alquiler.");
            return;
        }
        List<Game> games = new ArrayList<>();
        VBox content = (VBox) addedGameList.getContent();
        if (content != null){
            for(int i=0; i< content.getChildren().size(); i++) {
                Node child = content.getChildren().get(i);
                Object ud = child.getUserData();
                if (ud instanceof CartCardController existingCtrl && existingCtrl.quantity > 0) {
                    Game existingGame = existingCtrl.game;
                    games.add(new Game(existingGame.id, null, null, null, existingCtrl.quantity));
                }
            }
        }
        try {
            int clientId = Integer.parseInt(dniText);
            boolean ok = DBConnector.instance.processRental(new Rental(clientId, games));
            if (!ok) rentalFailureAlert("Hubo un error al procesar el alquiler. Por favor, inténtelo de nuevo.");
            else{
                rentalSuccessAlert();
                clearClientDetails();
                addedGameList.setContent(new VBox());
            }
        } catch (NumberFormatException _) {}
    }

    void rentalFailureAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error en el alquiler");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    void rentalSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alquiler procesado");
        alert.setHeaderText(null);
        alert.setContentText("El alquiler se ha procesado correctamente.");
        alert.show();
    }



}