package com.yarcrasy.gamex.Models;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

public class Rental {
    public int id;
    public int clientId;
    public LocalDate rentalDate;
    public float delayFee = 0;
    public List<Game> games = new ArrayList<>();

    public Rental(int id, int clientId, LocalDate rentalDate) {
        this.id = id;
        this.rentalDate = rentalDate;
        this.clientId = clientId;
    }

    public Rental(int clientId, List<Game> games) {
        this.clientId = clientId;
        this.games = games;
    }

    public void setDelayFee(float delayFee) {
        this.delayFee = delayFee;
    }

    public void addGame(Game game) {
        this.games.add(game);
    }

    // Getters para PropertyValueFactory
    public int getId() { return id; }
    public int getClientId() { return clientId; }
    public LocalDate getRentalDate() { return rentalDate; }
}