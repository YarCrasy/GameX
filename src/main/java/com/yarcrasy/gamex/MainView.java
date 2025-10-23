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
    public ScrollPane addedGameList;

    // Campos para búsqueda/autocompletado de clientes y detalle
    @FXML
    private TextField clientField;
    @FXML
    private Label clientIdLabel;
    @FXML
    private Label clientNameLabel;
    @FXML
    private Label clientAddressLabel;
    @FXML
    private Label clientDniLabel;
    @FXML
    private Label clientEmailLabel;
    @FXML
    private CheckBox clientFrequentCheckBox;

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

    // initialize se llama después de cargar el FXML
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
        if (clientIdLabel != null) clientIdLabel.setText("");
        if (clientNameLabel != null) clientNameLabel.setText("");
        if (clientAddressLabel != null) clientAddressLabel.setText("");
        if (clientDniLabel != null) clientDniLabel.setText("");
        if (clientEmailLabel != null) clientEmailLabel.setText("");
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

    public void onGameSearchBtnClick(ActionEvent e) {
        List<Game> gl = DBConnector.instance.getGamesByTitle(titleField.getText());
        updateGameList(gl);
    }

    public void onNewGameMenu(ActionEvent e) {
        openCreateNewWindow(ViewType.GAME);
    }

    public void onNewClientMenu(ActionEvent e) {
        openCreateNewWindow(ViewType.CLIENT);
    }

    public void onListGamesMenu(ActionEvent e) {
        openDisplayListWindow(ViewType.GAME);
    }

    public void onListClientsMenu(ActionEvent e) {
        openDisplayListWindow(ViewType.CLIENT);
    }

    public void onRentalsHistoryMenu(ActionEvent e) {
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
            com.yarcrasy.gamex.controllers.DisplayListController ctrl = loader.getController();
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

        if (existing == null) {
            content = new VBox();
        } else if (existing instanceof HBox) {
            content = (VBox) existing;
        } else if (existing instanceof javafx.scene.layout.Pane) {
            javafx.scene.layout.Pane pane = (javafx.scene.layout.Pane) existing;
            content = new VBox();
            content.getChildren().addAll(pane.getChildren());
        } else {
            content = new VBox();
        }

        addedGameList.setContent(content);
        content.setSpacing(1);
        CartCardController ccc = new CartCardController(this, g);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CartCard.fxml"));
            fxmlLoader.setController(ccc);
            Node gameCard = fxmlLoader.load();
            ccc.setCartCard(g);
            content.getChildren().add(gameCard);
        } catch (IOException e) {
            System.err.println("Error al añadir juego al carrito: " + e.getMessage());
        }
    }

}