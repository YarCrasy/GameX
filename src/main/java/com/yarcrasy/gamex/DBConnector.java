package com.yarcrasy.gamex;

import com.yarcrasy.gamex.Models.Client;
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
        StringBuilder sb = new StringBuilder();
        String delimiter = ";";

        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("#")) {
                    continue;
                }
                if (trimmed.toUpperCase().startsWith("DELIMITER")) {
                    // Cambiar delimitador (ej: DELIMITER //)
                    String[] parts = trimmed.split("\\s+", 2);
                    delimiter = parts.length > 1 ? parts[1] : ";";
                    continue;
                }
                sb.append(line).append("\n");
                String accumulated = sb.toString().trim();
                if (accumulated.endsWith(delimiter)) {
                    // quitar el delimitador final
                    String statement = accumulated.substring(0, accumulated.length() - delimiter.length()).trim();
                    if (!statement.isEmpty()) {
                        conn.createStatement().execute(statement);
                    }
                    sb.setLength(0);
                }
            }
            // ejecutar restante si lo hay
            String leftover = sb.toString().trim();
            if (!leftover.isEmpty()) {
                conn.createStatement().execute(leftover);
            }
        }
    }

    ResultSet exec(String sql) throws Exception {
        return conn.createStatement().executeQuery(sql);
    }

    Game setupGameFromResultSet(ResultSet rs) throws SQLException {
        Game game = new Game(
                rs.getString("idJuego"),
                rs.getString("titulo"),
                rs.getString("plataforma"),
                rs.getString("genero"),
                rs.getInt("stock")
        );
        return game;
    }

    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<>();
        try {
            ResultSet rs = exec("CALL GetGames();");
            while (rs.next()) {
                games.add(setupGameFromResultSet(rs));
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
                games.add(setupGameFromResultSet(rs));
            }
            return games;
        } catch (Exception e) {
            System.out.println(e);
        }
        return games;
    }

    Client setupClientFromResultSet(ResultSet rs) throws SQLException {
        Client client = new Client(
                rs.getString("idCliente"),
                rs.getString("dni"),
                rs.getString("nombreCompleto"),
                rs.getString("email"),
                rs.getString("direccion"),
                rs.getBoolean("esFrecuente")

        );
        return client;
    }

    public List<Client> getClientsByName(String name) {
        List<Client> clients = new ArrayList<>();
        try {
            ResultSet rs = exec("CALL GetClientsByName('" + name + "');");
            while (rs.next()) {
                clients.add(setupClientFromResultSet(rs));
            }
            return clients;
        } catch (Exception e) {
            System.out.println(e);
        }
        return clients;
    }

}
