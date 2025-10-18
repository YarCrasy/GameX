package com.yarcrasy.gamex;

import com.yarcrasy.gamex.Models.Game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBConnector {
    public static DBConnector instance;

    String database = "GameX";
    String urlBase = "jdbc:mysql://" + "localhost" + ":" + 3306 + "/";
    String user = "root";
    String password = "!StrongPassword123";

    Connection conn;

    public DBConnector() {
        instance = this;
        try { instance.connect(); }
        catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void connect() throws SQLException {
        String dbUrl = urlBase + database;
        try {
            conn = DriverManager.getConnection(dbUrl, user, password);
        } catch (Exception e) {
            try {
                initDatabase();
            } catch (Exception ex) {
                throw e;
            }
        }
        finally {
            conn = DriverManager.getConnection(dbUrl, user, password);
        }
    }

    void initDatabase() throws Exception {
        conn = DriverManager.getConnection(urlBase, user, password);
        String sql = "CREATE DATABASE IF NOT EXISTS " + database + ";";
        conn.createStatement().execute(sql);
        conn.close();
        conn = DriverManager.getConnection(urlBase + database, user, password);

        File file = new File("src/main/resources/com/yarcrasy/gamex/GameX.sql");
        StringBuilder sqlBuilder = new StringBuilder();
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                sqlBuilder.append(line).append("\n");
            }
        }

        String[] statements = sqlBuilder.toString().split(";");
        for (String statement : statements) {
            if (!statement.trim().isEmpty()) {
                conn.createStatement().execute(statement.trim());
            }
        }

    }

    ResultSet exec(String sql) throws Exception {
        return conn.createStatement().executeQuery(sql);
    }

    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<>();
        try {
            ResultSet rs = exec("CALL GetGames();");
            while (rs.next()) {
                Game game = new Game();
                game.id = rs.getString("idJuego");
                game.title = rs.getString("titulo");
                game.platform = rs.getString("plataforma");
                game.genre = rs.getString("genero");
                game.stock = rs.getInt("stock");
                games.add(game);
            }
            return games;
        } catch (Exception e) {
            System.out.println(e);
        }
        return games;
    }

    public List<Game> getGamesByTitle(String title) {
        List<Game> games = new ArrayList<>();
        try {
            ResultSet rs = exec("CALL GetGamesByTitle('" + title + "');");
            while (rs.next()) {
                Game game = new Game();
                game.id = rs.getString("idJuego");
                game.title = rs.getString("titulo");
                game.platform = rs.getString("plataforma");
                game.genre = rs.getString("genero");
                game.stock = rs.getInt("stock");
                games.add(game);
            }
            return games;
        } catch (Exception e) {
            System.out.println(e);
        }
        return games;
    }

}

