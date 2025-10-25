package com.yarcrasy.gamex;

import javafx.application.Application;

public class Launcher {
    static void main(String[] args) {
        new DBConnector();
        Application.launch(MainView.class, args);
    }
}
