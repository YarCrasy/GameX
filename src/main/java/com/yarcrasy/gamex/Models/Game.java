package com.yarcrasy.gamex.Models;

public class Game {
    public int id;
    public String title;
    public String platform;
    public String genre;
    public int stock;

    public Game(int id, String title, String platform, String genre, int stock) {
        this.id = id;
        this.title = title;
        this.platform = platform;
        this.genre = genre;
        this.stock = stock;
    }

    // Getters para PropertyValueFactory
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPlatform() {
        return platform;
    }

    public String getGenre() {
        return genre;
    }

    public int getStock() {
        return stock;
    }
}