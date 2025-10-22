package com.yarcrasy.gamex;

import com.yarcrasy.gamex.Models.Client;
import com.yarcrasy.gamex.Models.Game;
import com.yarcrasy.gamex.controllers.CartCardController;
import com.yarcrasy.gamex.controllers.GameCardController;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class MainView extends Application {

    @FXML
    private ScrollPane gameList;
    @FXML
    private TextField titleField;
    @FXML
    public ScrollPane addedGameList;

    // Nuevo: campos para búsqueda/autocompletado de clientes y detalle
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

    private ContextMenu clientSuggestions = new ContextMenu();
    private PauseTransition pause = new PauseTransition(Duration.millis(200)); // debounce

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
        // Configurar autocompletado para clientField
        if (clientField != null) {
            clientField.textProperty().addListener((obs, oldText, newText) -> {
                pause.stop();
                pause.setOnFinished(ev -> showClientSuggestions(newText));
                pause.playFromStart();
            });

            clientField.setOnKeyPressed(ev -> {
                if (ev.getCode() == KeyCode.DOWN) {
                    if (!clientSuggestions.isShowing() && !clientSuggestions.getItems().isEmpty()) {
                        clientSuggestions.show(clientField, Side.BOTTOM, 0, 0);
                    }
                }
            });
        }

        // limpiar detalles al inicio
        clearClientDetails();
    }

    private void showClientSuggestions(String query) {
        clientSuggestions.hide();
        if (query == null || query.trim().isEmpty()) {
            return;
        }

        final String currentQuery = query;

        Task<List<Client>> task = new Task<>() {
            @Override
            protected List<Client> call() {
                return DBConnector.instance.getClientsByNameOrDni(currentQuery);
            }
        };

        task.setOnSucceeded(ev -> {
            List<Client> clients = task.getValue();
            // si la consulta cambió, ignorar estos resultados
            if (!currentQuery.equals(clientField.getText())) {
                return;
            }
            if (clients == null || clients.isEmpty()) {
                return;
            }
            ContextMenu menu = new ContextMenu();
            for (Client c : clients) {
                Label lbl = new Label(c.fullName + "  (" + c.dni + ")");
                CustomMenuItem item = new CustomMenuItem(lbl, true);
                item.setOnAction(ae -> {
                    clientField.setText(c.fullName);
                    populateClientDetails(c);
                    menu.hide();
                });
                menu.getItems().add(item);
            }
            // mostrar en hilo de UI
            Platform.runLater(() -> {
                clientSuggestions = menu;
                if (!menu.getItems().isEmpty()) {
                    menu.show(clientField, Side.BOTTOM, 0, 0);
                }
            });
        });

        task.setOnFailed(ev -> {
            // No bloquear UI si la consulta falla; opcionalmente loguear
            // ev.getSource().getException().printStackTrace();
        });

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
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
            e.printStackTrace();
        }
    }

}