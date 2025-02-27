package me.ancastanoev.database;

import me.ancastanoev.Guide;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuideDAO {

    public void createGuidesTable(String role) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can create tables.");
            return;
        }

        String createTableSQL = "CREATE TABLE IF NOT EXISTS guides (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "experience_level TEXT NOT NULL" +
                ");";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Guides table created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addGuide(String role, Guide guide) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can add guides.");
            return;
        }

        String insertSQL = "INSERT INTO guides (name, experience_level) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, sanitizeInput(guide.getName()));
            pstmt.setString(2, sanitizeInput(guide.getExperienceLevel()));
            pstmt.executeUpdate();
            System.out.println("Guide added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Guide getGuideById(String role, int id) {
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("user")) {
            System.out.println("Permission denied: Invalid role.");
            return null;
        }

        String querySQL = "SELECT * FROM guides WHERE id = ?";
        Guide guide = null;
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(querySQL)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String experienceLevel = rs.getString("experience_level");
                    guide = new Guide(sanitizeInput(name), sanitizeInput(experienceLevel));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guide;
    }

    public List<Guide> getAllGuides(String role) {
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("user")) {
            System.out.println("Permission denied: Invalid role.");
            return new ArrayList<>();
        }

        String querySQL = "SELECT * FROM guides";
        List<Guide> guides = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            while (rs.next()) {
                String name = rs.getString("name");
                String experienceLevel = rs.getString("experience_level");
                Guide guide = new Guide(sanitizeInput(name), sanitizeInput(experienceLevel));
                guides.add(guide);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guides;
    }

    public void updateGuide(String role, int id, Guide updatedGuide) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can update guides.");
            return;
        }

        String updateSQL = "UPDATE guides SET name = ?, experience_level = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, sanitizeInput(updatedGuide.getName()));
            pstmt.setString(2, sanitizeInput(updatedGuide.getExperienceLevel()));
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            System.out.println("Guide updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGuide(String role, int id) {
        if (!role.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can delete guides.");
            return;
        }

        String deleteSQL = "DELETE FROM guides WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Guide deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Instance method to find a guide by route difficulty.
    public Guide findGuideByDifficulty(String role, String routeDifficulty) {
        if (!role.equalsIgnoreCase("user") && !role.equalsIgnoreCase("admin")) {
            throw new SecurityException("Access denied: Invalid role.");
        }
        String sql = "SELECT * FROM guides WHERE experience_level = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // For simplicity, we map route difficulty to a required guide experience:
            // "easy" → "Beginner", "moderate" → "Intermediate", "difficult" → "Expert"
            pstmt.setString(1, sanitizeInput(getGuideExperience(routeDifficulty)));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String experienceLevel = rs.getString("experience_level");
                    return new Guide(sanitizeInput(name), sanitizeInput(experienceLevel));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- New static helper method: getGuideForLevel ---
    public static String getGuideForLevel(String experienceLevel, String difficulty) {
        // For simplicity, we assume the role "user" for fetching a guide.
        GuideDAO dao = new GuideDAO();
        Guide guide = dao.findGuideByDifficulty("user", difficulty);
        return (guide != null) ? guide.getName() : "";
    }

    private String getGuideExperience(String routeDifficulty) {
        switch (routeDifficulty.toLowerCase()) {
            case "easy":
                return "Beginner";
            case "moderate":
                return "Intermediate";
            case "difficult":
                return "Expert";
            default:
                return "Beginner";
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
