package com.yarcrasy.gamex;

import com.yarcrasy.gamex.Models.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
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
                    String[] parts = trimmed.split("\\s+", 2);
                    delimiter = parts.length > 1 ? parts[1] : ";";
                    continue;
                }
                sb.append(line).append("\n");
                String accumulated = sb.toString().trim();
                if (accumulated.endsWith(delimiter)) {
                    String statement = accumulated.substring(0, accumulated.length() - delimiter.length()).trim();
                    if (!statement.isEmpty()) {
                        conn.createStatement().execute(statement);
                    }
                    sb.setLength(0);
                }
            }
            String leftover = sb.toString().trim();
            if (!leftover.isEmpty()) {
                conn.createStatement().execute(leftover);
            }
        }
    }

    Game setupGameFromResultSet(ResultSet rs) throws SQLException {
        return new Game(
                rs.getString("idJuego"),
                rs.getString("titulo"),
                rs.getString("plataforma"),
                rs.getString("genero"),
                rs.getInt("stock")
        );
    }

    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Juego;");
            ResultSet rs = ps.executeQuery();
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
            PreparedStatement ps = conn.prepareStatement("CALL GetGamesByTitle(?);");
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
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
        return new Client(
                rs.getString("idCliente"),
                rs.getString("dni"),
                rs.getString("nombreCompleto"),
                rs.getString("email"),
                rs.getString("direccion"),
                rs.getBoolean("esFrecuente")
        );
    }

    public List<Client> getClientsByNameOrDni(String data) {
        List<Client> clients = new ArrayList<>();
        if (data == null) return clients;
        try {
            PreparedStatement ps = conn.prepareStatement("CALL GetClientByNameOrDNI(?)");
            ps.setString(1, data);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                clients.add(setupClientFromResultSet(rs));
            }
            return clients;
        } catch (Exception e) {
            System.err.println(e);
        }
        return clients;
    }

    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Cliente;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                clients.add(setupClientFromResultSet(rs));
            }
            return clients;
        } catch (Exception e) {
            System.out.println(e);
        }
        return clients;
    }

    public List<Rental> getAllRentals() {
        List<Rental> rentals = new ArrayList<>();
        return rentals;
    }

    public boolean addGame(String title, String platform, double price, String genre, int stock) {
        try (PreparedStatement ps = conn.prepareStatement(
                "CALL AddGame(?, ?, ?, ?, ?)")) {
            ps.setString(1, title);
            ps.setString(2, platform);
            ps.setDouble(3, price);
            ps.setString(4, genre);
            ps.setInt(5, stock);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean addClient(String dni, String fullName, String email, String address) {
        try (PreparedStatement ps = conn.prepareStatement(
                "CALL AddClient(?, ?, ?, ?)")) {
            ps.setString(1, dni);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setString(4, address);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
}