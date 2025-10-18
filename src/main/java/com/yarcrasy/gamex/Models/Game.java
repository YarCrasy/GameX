package com.yarcrasy.gamex.Models;

public class Game {
    public String id;
    public String title;
    public String platform;
    public String genre;
    public int stock;

    public Game(String id, String title, String platform, String genre, int stock) {
        this.id = id;
        this.title = title;
        this.platform = platform;
        this.genre = genre;
        this.stock = stock;
    }

}
