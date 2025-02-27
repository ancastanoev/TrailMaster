package me.ancastanoev.database;

import me.ancastanoev.Route;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RouteDAO {

    public void createRoutesTable(String role) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can create tables.");
            return;
        }

        String createTableSQL = "CREATE TABLE IF NOT EXISTS routes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "difficulty TEXT NOT NULL, " +
                "mountain_name TEXT NOT NULL" +
                ");";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Routes table created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addRoute(String role, Route route) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can add routes.");
            return;
        }

        String insertSQL = "INSERT INTO routes (name, difficulty, mountain_name) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, sanitizeInput(route.getName()));
            pstmt.setString(2, sanitizeInput(route.getDifficultyLevel()));
            pstmt.setString(3, sanitizeInput(route.getMountainName()));

            pstmt.executeUpdate();
            System.out.println("Route added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Route getRouteById(String role, int id) {
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("user")) {
            System.out.println("Permission denied: Invalid role.");
            return null;
        }

        String querySQL = "SELECT * FROM routes WHERE id = ?";
        Route route = null;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(querySQL)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String difficultyLevel = rs.getString("difficulty");
                    String mountainName = rs.getString("mountain_name");

                    route = new Route(
                            sanitizeInput(name),
                            sanitizeInput(difficultyLevel),
                            sanitizeInput(mountainName)
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return route;
    }

    public List<Route> getAllRoutes(String role) {
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("user")) {
            System.out.println("Permission denied: Invalid role.");
            return new ArrayList<>();
        }

        String query = "SELECT * FROM routes";
        List<Route> routes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String name = rs.getString("name");
                String difficulty = rs.getString("difficulty");
                String mountainName = rs.getString("mountain_name");
                Route route = new Route(
                        sanitizeInput(name),
                        sanitizeInput(difficulty),
                        sanitizeInput(mountainName)
                );
                routes.add(route);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }

    public void updateRoute(String role, int id, Route updatedRoute) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can update routes.");
            return;
        }

        String updateSQL = "UPDATE routes SET name = ?, difficulty = ?, mountain_name = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, sanitizeInput(updatedRoute.getName()));
            pstmt.setString(2, sanitizeInput(updatedRoute.getDifficultyLevel()));
            pstmt.setString(3, sanitizeInput(updatedRoute.getMountainName()));
            pstmt.setInt(4, id);

            pstmt.executeUpdate();
            System.out.println("Route updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRoute(String role, int id) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can delete routes.");
            return;
        }

        String deleteSQL = "DELETE FROM routes WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Route deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Route getRouteByName(String role, String routeName) {
        if (!role.equalsIgnoreCase("user") && !role.equalsIgnoreCase("admin")) {
            throw new SecurityException("Access denied: Invalid role.");
        }

        String querySQL = "SELECT * FROM routes WHERE name = ?";
        Route route = null;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(querySQL)) {

            pstmt.setString(1, sanitizeInput(routeName));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String difficultyLevel = rs.getString("difficulty");
                    String mountainName = rs.getString("mountain_name");

                    route = new Route(
                            sanitizeInput(name),
                            sanitizeInput(difficultyLevel),
                            sanitizeInput(mountainName)
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return route;
    }
    public boolean updateRoute(String role,
                               String oldName,
                               String oldDifficulty,
                               String oldMountain,
                               Route updatedRoute) {
        if (!"admin".equalsIgnoreCase(role)) {
            System.out.println("Permission denied: Only admins can update routes.");
            return false;
        }

        String sql = "UPDATE routes " +
                "SET name = ?, difficulty = ?, mountain = ? " +
                "WHERE name = ? AND difficulty = ? AND mountain = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // New fields
            stmt.setString(1, updatedRoute.getName());
            stmt.setString(2, updatedRoute.getDifficultyLevel());
            stmt.setString(3, updatedRoute.getMountainName());

            // Old fields in the WHERE clause
            stmt.setString(4, oldName);
            stmt.setString(5, oldDifficulty);
            stmt.setString(6, oldMountain);

            int rows = stmt.executeUpdate();
            return (rows > 0);
        } catch (SQLException e) {
            System.err.println("Error updating route: " + e.getMessage());
            return false;
        }
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
