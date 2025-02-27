package me.ancastanoev.database;

import me.ancastanoev.Expedition;
import me.ancastanoev.Route;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpeditionDAO {

    public void createExpeditionsTable(String role) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can create tables.");
            return;
        }

        String createTableSQL = "CREATE TABLE IF NOT EXISTS expeditions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "start_date TEXT NOT NULL, " +
                "end_date TEXT NOT NULL, " +
                "difficulty_level TEXT NOT NULL, " +
                "route_name TEXT NOT NULL" +
                ");";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Expeditions table created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addExpedition(String role, Expedition expedition) {
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("user")) {
            System.out.println("Permission denied: Only users or admins can add expeditions.");
            return false;
        }

        String insertSQL = "INSERT INTO expeditions (start_date, end_date, difficulty_level, route_name) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, sanitizeInput(expedition.getStartDate()));
            pstmt.setString(2, sanitizeInput(expedition.getEndDate()));
            pstmt.setString(3, sanitizeInput(expedition.getDifficultyLevel()));
            pstmt.setString(4, sanitizeInput(expedition.getRoute().getName()));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Expedition added successfully!");
                return true;
            } else {
                System.out.println("Failed to add the expedition.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Expedition getExpeditionById(String role, int id) {
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("user")) {
            System.out.println("Permission denied: Invalid role.");
            return null;
        }

        String querySQL = "SELECT * FROM expeditions WHERE id = ?";
        Expedition expedition = null;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(querySQL)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String startDate = rs.getString("start_date");
                    String endDate = rs.getString("end_date");
                    String difficultyLevel = rs.getString("difficulty_level");
                    String routeName = rs.getString("route_name");

                    Route route = Route.fromText(sanitizeInput(routeName));
                    expedition = new Expedition(
                            sanitizeInput(startDate),
                            sanitizeInput(endDate),
                            sanitizeInput(difficultyLevel),
                            route
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expedition;
    }

    public void updateExpedition(String role, int id, Expedition updatedExpedition) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can update expeditions.");
            return;
        }

        String updateSQL = "UPDATE expeditions SET start_date = ?, end_date = ?, difficulty_level = ?, route_name = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, sanitizeInput(updatedExpedition.getStartDate()));
            pstmt.setString(2, sanitizeInput(updatedExpedition.getEndDate()));
            pstmt.setString(3, sanitizeInput(updatedExpedition.getDifficultyLevel()));
            pstmt.setString(4, sanitizeInput(updatedExpedition.getRoute().getName()));
            pstmt.setInt(5, id);

            pstmt.executeUpdate();
            System.out.println("Expedition updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteExpedition(String role, int id) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can delete expeditions.");
            return;
        }

        String deleteSQL = "DELETE FROM expeditions WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Expedition deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Expedition> getAllExpeditions(String role) {
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("user")) {
            System.out.println("Permission denied: Invalid role.");
            return new ArrayList<>();
        }

        String querySQL = "SELECT * FROM expeditions";
        List<Expedition> expeditions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            while (rs.next()) {
                String startDate = rs.getString("start_date");
                String endDate = rs.getString("end_date");
                String difficultyLevel = rs.getString("difficulty_level");
                String routeName = rs.getString("route_name");

                Route route = Route.fromText(sanitizeInput(routeName));
                Expedition expedition = new Expedition(
                        sanitizeInput(startDate),
                        sanitizeInput(endDate),
                        sanitizeInput(difficultyLevel),
                        route
                );
                expeditions.add(expedition);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expeditions;
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
