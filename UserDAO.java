package me.ancastanoev.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public void createUsersTable(String role) {
        if (!"admin".equalsIgnoreCase(role)) {
            System.out.println("Permission denied: Only admin users can create the users table.");
            return;
        }

        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL CHECK (role IN ('Admin', 'User'))" +
                ");";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
            System.out.println("Users table created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating Users table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean authenticateUser(String username, String password) {
        // Use sanitized inputs
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, sanitizeInput(username));
            pstmt.setString(2, sanitizeInput(password));

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean authenticateUserNoPassword(String username) {
        String query = "SELECT * FROM users WHERE username = ? AND role = 'User'";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, sanitizeInput(username));

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error authenticating user (no password): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public String getUserRole(String username) {
        String query = "SELECT role FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, sanitizeInput(username));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user role: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean createUser(String username, String password, String role) {
        // Sanitize everything
        String insertSQL = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, sanitizeInput(username));
            pstmt.setString(2, sanitizeInput(password));
            pstmt.setString(3, sanitizeInput(role));

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Error: Username already exists.");
            } else {
                System.out.println("Error creating user: " + e.getMessage());
            }
            e.printStackTrace();
        }
        return false;
    }

    public boolean createUserNoPassword(String username) {
        String insertSQL = "INSERT INTO users (username, password, role) VALUES (?, ?, 'User')";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, sanitizeInput(username));
            pstmt.setString(2, "");

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Error: Username already exists.");
            } else {
                System.out.println("Error creating user: " + e.getMessage());
            }
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Basic sanitization to avoid malicious input (e.g., "DROP TABLE").
     */
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
