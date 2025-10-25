package com.yarcrasy.gamex;

import com.yarcrasy.gamex.Models.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnector {
    public static DBConnector instance;

    final String database = "GameX";
    final String urlBase = "jdbc:mysql://" + "localhost" + ":" + 3306 + "/";
    final String user = "root";
    final String password = "!StrongPassword123";

    Connection conn;

    public DBConnector() {
        instance = this;
        instance.connect();
    }

    public void connect() {
        String dbUrl = urlBase + database;
        try {
            conn = DriverManager.getConnection(dbUrl, user, password);
        } catch (SQLException e) {
            if (!initDatabase()) System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    boolean initDatabase() {
        try {
            conn = DriverManager.getConnection(urlBase, user, password);
            String sql = "CREATE DATABASE IF NOT EXISTS " + database + ";";
            conn.createStatement().execute(sql);
            conn.close();
            conn = DriverManager.getConnection(urlBase + database, user, password);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            return false;
        }

        try {
            setupDBFromFile();
        } catch (IOException | SQLException e) {
            System.err.println("Error reading SQL file: " + e.getMessage());
            return false;
        }
        return true;
    }

    void setupDBFromFile() throws IOException, SQLException {
        File file = new File("src/main/resources/com/yarcrasy/gamex/GameX.sql");
        StringBuilder sb = new StringBuilder();
        String delimiter = ";";

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
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

    public void rstDataBase() {
        try {
            conn = DriverManager.getConnection(urlBase, user, password);
            String sql = "DROP DATABASE IF EXISTS " + database + ";";
            conn.prepareStatement(sql);
            connect();
        }
        catch (SQLException e) {
            System.err.println("Error resetting database: " + e.getMessage());
        }
    }

    Game setupGameFromResultSet(ResultSet rs) throws SQLException {
        return new Game(
                rs.getInt("idJuego"),
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
            System.err.println(e.getMessage());
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
            System.err.println(e.getMessage());
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
            System.err.println(e.getMessage());
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
            System.err.println(e.getMessage());
        }
        return clients;
    }

    Rental setupRentalFromResultSet(ResultSet rs) throws SQLException {
        Rental r = new Rental(
                rs.getInt("idAlquiler"),
                rs.getInt("idCliente"),
                rs.getDate("fechaAlquiler").toLocalDate()
        );
        r.setDelayFee(rs.getFloat("multaRetraso"));
        return r;
    }

    public List<Rental> getAllRentals() {
        List<Rental> rentals = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Alquiler;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rentals.add(setupRentalFromResultSet(rs));
            }
            return rentals;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return rentals;
    }

    public boolean addGame(String title, String platform, float price, String genre, int stock) {
        try (PreparedStatement ps = conn.prepareStatement("CALL AddGame(?, ?, ?, ?, ?)")) {
            ps.setString(1, title);
            ps.setString(2, platform);
            ps.setFloat(3, price);
            ps.setString(4, genre);
            ps.setInt(5, stock);
            ps.executeQuery();
            ps.close();
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
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
            ps.executeQuery();
            ps.close();
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean processRental(Rental r) {
        String addRentalSql = "CALL AddRental(?, ?)";
        String addGameSql = "CALL AddGameToRental(?, ?, ?)";
        boolean previousAutoCommit;
        try {
            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            CallableStatement cs = conn.prepareCall(addRentalSql);
            cs.setInt(1, r.clientId);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            int rentalId = cs.getInt(2);
            cs.close();

            PreparedStatement ps = conn.prepareStatement(addGameSql);
            for (Game game : r.games) {
                ps.setInt(1, rentalId);
                ps.setInt(2, game.id);
                ps.setInt(3, game.stock);
                ps.execute();
            }
            ps.close();

            conn.commit();
            conn.setAutoCommit(previousAutoCommit);
            return true;
        } catch (Exception e) {
            System.err.println("Error processing rental: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        }
        return false;
    }
}