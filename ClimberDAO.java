package me.ancastanoev.database;

import me.ancastanoev.Climber;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClimberDAO {

    // Role validation method
    private boolean hasPermission(String role, String operation) {
        if (role == null || role.isEmpty()) {
            throw new IllegalArgumentException("Role must not be null or empty.");
        }
        switch (role.toLowerCase()) {
            case "admin":
                return true;
            case "user":
                return operation.equals("view")
                        || operation.equals("plan")
                        || operation.equals("add")
                        || operation.equals("update");
            default:
                throw new SecurityException("Unauthorized role: " + role);
        }
    }

    public void createClimbersTable(String role) {
        if (!hasPermission(role, "create")) {
            throw new SecurityException("You do not have permission to create the climbers table.");
        }

        String createTableSQL = "CREATE TABLE IF NOT EXISTS climbers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT NOT NULL, " +
                "last_name TEXT NOT NULL, " +
                "experience_level TEXT NOT NULL, " +
                "contact_info TEXT NOT NULL" +
                ");";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Climbers table created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addClimber(String role, Climber climber) {
        if (!hasPermission(role, "add")) {
            throw new SecurityException("You do not have permission to add a climber.");
        }

        String query = "INSERT INTO climbers (first_name, last_name, experience_level, contact_info) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Validate/sanitize each field before putting it into the statement
            pstmt.setString(1, sanitizeInput(climber.getFirstName()));
            pstmt.setString(2, sanitizeInput(climber.getLastName()));
            pstmt.setString(3, sanitizeInput(climber.getExperienceLevel()));
            pstmt.setString(4, sanitizeInput(climber.getContactInfo()));

            pstmt.executeUpdate();
            System.out.println("Climber added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Climber> getAllClimbers(String role) {
        if (!role.equalsIgnoreCase("user") && !role.equalsIgnoreCase("admin")) {
            throw new SecurityException("Access denied: Invalid role.");
        }

        String query = "SELECT * FROM climbers";
        List<Climber> climbers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Climber climber = new Climber(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("experience_level"),
                        rs.getString("contact_info")
                );
                climbers.add(climber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return climbers;
    }

    public boolean updateExperienceLevel(String role, String firstName, String lastName, String newExperience) {
        if (!hasPermission(role, "update")) {
            throw new SecurityException("You do not have permission to update a climber's experience level.");
        }

        String query = "UPDATE climbers SET experience_level = ? WHERE first_name = ? AND last_name = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, sanitizeInput(newExperience));
            pstmt.setString(2, sanitizeInput(firstName));
            pstmt.setString(3, sanitizeInput(lastName));

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Basic input validation method to prevent malicious SQL commands.
     */
    private static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Adjust the whitelist to allow typical email chars:
        // letters, digits, underscore, dot, plus, hyphen, and '@'
        if (!input.matches("[A-Za-z0-9@._+\\-]*")) {
            throw new IllegalArgumentException("Invalid characters in input: " + input);
        }

        String lower = input.toLowerCase();
        if (lower.contains("drop table") || lower.contains("delete from")) {
            throw new IllegalArgumentException("Potentially malicious keyword in input: " + input);
        }
        return input;
    }

}
