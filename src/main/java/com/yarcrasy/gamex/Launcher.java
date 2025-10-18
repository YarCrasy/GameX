package com.yarcrasy.gamex;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        new DBConnector();
        Application.launch(MainView.class, args);
    }
}
