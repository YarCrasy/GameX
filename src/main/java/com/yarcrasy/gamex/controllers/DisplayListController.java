package com.yarcrasy.gamex.controllers;

import com.yarcrasy.gamex.DBConnector;
import com.yarcrasy.gamex.Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DisplayListController {

    @FXML
    private Label titleLabel;

    @FXML
    private TableView tableView;

    private ViewType type = ViewType.GAME;

    public void setType(ViewType type) {
        this.type = type;
        setupTable();
        loadData();
    }

    private void setupTable() {
        tableView.getColumns().clear();
        if (type == ViewType.GAME) {
            titleLabel.setText("Lista de juegos");
            TableColumn<Game, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<Game, String> titleCol = new TableColumn<>("Título");
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            TableColumn<Game, String> platCol = new TableColumn<>("Plataforma");
            platCol.setCellValueFactory(new PropertyValueFactory<>("platform"));
            TableColumn<Game, String> genreCol = new TableColumn<>("Género");
            genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
            TableColumn<Game, Integer> stockCol = new TableColumn<>("Stock");
            stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
            tableView.getColumns().addAll(idCol, titleCol, platCol, genreCol, stockCol);
        }
        else if (type == ViewType.RENTAL) {
            titleLabel.setText("Lista de clientes");
            TableColumn<Client, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<Client, String> dniCol = new TableColumn<>("DNI");
            dniCol.setCellValueFactory(new PropertyValueFactory<>("dni"));
            TableColumn<Client, String> nameCol = new TableColumn<>("Nombre");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            TableColumn<Client, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            TableColumn<Client, String> addressCol = new TableColumn<>("Dirección");
            addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
            TableColumn<Client, Boolean> freqCol = new TableColumn<>("Frecuente");
            freqCol.setCellValueFactory(new PropertyValueFactory<>("isFrequent"));
            tableView.getColumns().addAll(idCol, dniCol, nameCol, emailCol, addressCol, freqCol);
        } else {
            titleLabel.setText("Historial de alquileres");
            TableColumn<Rental, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<Rental, String> clientCol = new TableColumn<>("Cliente");
            clientCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue() != null && ((Rental) cellData.getValue()).client != null ? ((Rental) cellData.getValue()).client.fullName : ""));
            TableColumn<Rental, String> dateCol = new TableColumn<>("Fecha");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("rentalDate"));
            TableColumn<Rental, Float> totalCol = new TableColumn<>("Total");
            totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            TableColumn<Rental, Boolean> delayedCol = new TableColumn<>("Retraso");
            delayedCol.setCellValueFactory(new PropertyValueFactory<>("isDelayed"));
            tableView.getColumns().addAll(idCol, clientCol, dateCol, totalCol, delayedCol);
        }
    }

    private void loadData() {
        if (type == ViewType.GAME) {
            java.util.List<Game> games = DBConnector.instance.getAllGames();
            ObservableList<Game> items = FXCollections.observableArrayList(games);
            tableView.setItems(items);
        }
        else if (type == ViewType.RENTAL) {
            java.util.List<Client> clients = DBConnector.instance.getAllClients();
            ObservableList<Client> items = FXCollections.observableArrayList(clients);
            tableView.setItems(items);
        }
        else {
            java.util.List<Rental> rentals = DBConnector.instance.getAllRentals();
            ObservableList<Rental> items = FXCollections.observableArrayList(rentals);
            tableView.setItems(items);
        }
    }

}
