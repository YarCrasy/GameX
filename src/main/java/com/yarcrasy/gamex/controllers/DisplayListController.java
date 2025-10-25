package com.yarcrasy.gamex.controllers;

import com.yarcrasy.gamex.DBConnector;
import com.yarcrasy.gamex.Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Date;

public class DisplayListController {

    @FXML
    public TableView tableView;

    private ViewType type = ViewType.GAME;

    public void setType(ViewType type) {
        this.type = type;
        setupTable();
        loadData();
    }

    private void setupTable() {
        tableView.getColumns().clear();
        if (type == ViewType.GAME) {
            TableColumn<Game, Integer> idCol = new TableColumn<>("ID");
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
        else if (type == ViewType.CLIENT) {
            TableColumn<Client, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<Client, String> dniCol = new TableColumn<>("DNI");
            dniCol.setCellValueFactory(new PropertyValueFactory<>("dni"));
            TableColumn<Client, String> nameCol = new TableColumn<>("Nombre Completo");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            TableColumn<Client, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            TableColumn<Client, String> addressCol = new TableColumn<>("Dirección");
            addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
            TableColumn<Client, Boolean> freqCol = new TableColumn<>("Frecuente");
            freqCol.setCellValueFactory(new PropertyValueFactory<>("isFrequent"));
            tableView.getColumns().addAll(idCol, dniCol, nameCol, emailCol, addressCol, freqCol);
        }
        else {
            TableColumn<Rental, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<Rental, String> clientIdCol = new TableColumn<>("Cliente");
            clientIdCol.setCellValueFactory(new PropertyValueFactory<>("clientId"));
            TableColumn<Rental, Date> dateCol = new TableColumn<>("Fecha");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("rentalDate"));

            tableView.getColumns().addAll(idCol, clientIdCol, dateCol);
        }
    }

    private void loadData() {
        if (type == ViewType.GAME) {
            java.util.List<Game> games = DBConnector.instance.getAllGames();
            ObservableList<Game> items = FXCollections.observableArrayList(games);
            tableView.setItems(items);
        }
        else if (type == ViewType.CLIENT) {
            java.util.List<Client> clients = DBConnector.instance.getAllClients();
            ObservableList<Client> items = FXCollections.observableArrayList(clients);
            tableView.setItems(items);
        }
        else if (type == ViewType.RENTAL) {
            java.util.List<Rental> rentals = DBConnector.instance.getAllRentals();
            ObservableList<Rental> items = FXCollections.observableArrayList(rentals);
            tableView.setItems(items);
        }
    }

}