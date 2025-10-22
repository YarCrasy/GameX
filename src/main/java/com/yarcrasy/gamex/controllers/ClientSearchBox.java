package com.yarcrasy.gamex.controllers;

import com.yarcrasy.gamex.DBConnector;
import com.yarcrasy.gamex.Models.Client;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Componente reutilizable que añade autocompletado en tiempo real a un TextField
 * buscando clientes en la base de datos y llamando a un callback cuando se
 * selecciona un cliente.
 */
public class ClientSearchBox {

    private final TextField field;
    private final Consumer<Client> onClientSelected;
    private ContextMenu suggestions = new ContextMenu();
    private final PauseTransition pause = new PauseTransition(Duration.millis(200));

    public ClientSearchBox(TextField field, Consumer<Client> onClientSelected) {
        this.field = Objects.requireNonNull(field);
        this.onClientSelected = onClientSelected;
        install();
    }

    private void install() {
        field.textProperty().addListener((obs, oldText, newText) -> {
            pause.stop();
            pause.setOnFinished(ev -> queryAndShow(newText));
            pause.playFromStart();
        });

        // Hide suggestions when focus lost
        field.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                suggestions.hide();
            }
        });
    }

    private void queryAndShow(String query) {
        suggestions.hide();
        if (query == null || query.trim().isEmpty()) return;

        final String currentQuery = query;

        Task<List<Client>> task = new Task<>() {
            @Override
            protected List<Client> call() {
                // Llama al DBConnector (método getClientsByNameOrDni debe existir)
                return DBConnector.instance.getClientsByNameOrDni(currentQuery);
            }
        };

        task.setOnSucceeded(ev -> {
            List<Client> clients = task.getValue();
            if (!currentQuery.equals(field.getText())) return; // resultados obsoletos
            if (clients == null || clients.isEmpty()) return;

            ContextMenu menu = new ContextMenu();
            for (Client c : clients) {
                Label lbl = new Label(c.fullName + "  (" + c.dni + ")");
                CustomMenuItem item = new CustomMenuItem(lbl, true);
                item.setOnAction(ae -> {
                    field.setText(c.fullName);
                    if (onClientSelected != null) {
                        onClientSelected.accept(c);
                    }
                    menu.hide();
                });
                menu.getItems().add(item);
            }

            Platform.runLater(() -> {
                suggestions = menu;
                if (!menu.getItems().isEmpty()) {
                    menu.show(field, Side.BOTTOM, 0, 0);
                }
            });
        });

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }
}
