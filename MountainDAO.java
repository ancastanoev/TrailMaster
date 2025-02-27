package me.ancastanoev.database;

import me.ancastanoev.Mountain;
import me.ancastanoev.Expedition;
import me.ancastanoev.Route;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MountainDAO {

    public void createMountainsTable(String role) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can create tables.");
            return;
        }

        String createTableSQL = "CREATE TABLE IF NOT EXISTS mountains (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "expeditions TEXT, " +
                "routes TEXT" +
                ");";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Mountains table created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMountain(String role, Mountain mountain) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can add mountains.");
            return;
        }

        String insertSQL = "INSERT INTO mountains (name, expeditions, routes) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, sanitizeInput(mountain.getName()));
            pstmt.setString(2, sanitizeInput(serializeExpeditions(mountain.getExpeditions())));
            pstmt.setString(3, sanitizeInput(serializeRoutes(mountain.getRoutes())));

            pstmt.executeUpdate();
            System.out.println("Mountain added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Mountain> getAllMountains(String role) {
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("user")) {
            System.out.println("Permission denied: Invalid role.");
            return new ArrayList<>();
        }

        String querySQL = "SELECT * FROM mountains";
        List<Mountain> mountains = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            while (rs.next()) {
                String name = rs.getString("name");
                String expeditionsSerialized = rs.getString("expeditions");
                String routesSerialized = rs.getString("routes");

                Mountain mountain = new Mountain(sanitizeInput(name));
                mountain.getExpeditions().addAll(deserializeExpeditions(expeditionsSerialized));
                mountain.getRoutes().addAll(deserializeRoutes(routesSerialized));
                mountains.add(mountain);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mountains;
    }

    public void deleteMountain(String role, String name) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can delete mountains.");
            return;
        }

        String deleteSQL = "DELETE FROM mountains WHERE name = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setString(1, sanitizeInput(name));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Mountain deleted successfully!");
            } else {
                System.out.println("No mountain found with the specified name.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String serializeExpeditions(List<Expedition> expeditions) {
        if (expeditions == null || expeditions.isEmpty()) {
            return null;
        }
        StringBuilder serialized = new StringBuilder();
        for (Expedition expedition : expeditions) {
            // sanitize each toText() output
            serialized.append(sanitizeInput(expedition.toText())).append(";");
        }
        return serialized.toString();
    }

    private List<Expedition> deserializeExpeditions(String serialized) {
        List<Expedition> expeditions = new ArrayList<>();
        if (serialized != null && !serialized.isEmpty()) {
            String[] parts = serialized.split(";");
            for (String part : parts) {
                expeditions.add(Expedition.fromText(part));
            }
        }
        return expeditions;
    }

    private String serializeRoutes(List<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            return null;
        }
        StringBuilder serialized = new StringBuilder();
        for (Route route : routes) {
            serialized.append(sanitizeInput(route.toText())).append(";");
        }
        return serialized.toString();
    }

    private List<Route> deserializeRoutes(String serialized) {
        List<Route> routes = new ArrayList<>();
        if (serialized != null && !serialized.isEmpty()) {
            String[] parts = serialized.split(";");
            for (String part : parts) {
                routes.add(Route.fromText(part));
            }
        }
        return routes;
    }

    private static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        if (!input.matches("[A-Za-z0-9 _-]*")) {
            throw new IllegalArgumentException("Invalid characters in input: " + input);
        }
        String lower = input.toLowerCase();
        if (lower.contains("drop table") || lower.contains("delete from")) {
            throw new IllegalArgumentException("Potentially malicious keyword in input: " + input);
        }
        return input;
    }
}
