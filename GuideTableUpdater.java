package me.ancastanoev.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class GuideTableUpdater {

    public static void updateGuideTableSchema() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            // Step 1: Create a new table with the correct schema
            String createNewTableSQL = "CREATE TABLE IF NOT EXISTS guides_new (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "experience_level TEXT NOT NULL" +
                    ");";
            stmt.execute(createNewTableSQL);

            // Step 2: Copy data from the old table to the new table
            String copyDataSQL = "INSERT INTO guides_new (id, name, experience_level) " +
                    "SELECT id, name, 'Beginner' FROM guides;";
            stmt.execute(copyDataSQL);

            // Step 3: Drop the old table
            String dropOldTableSQL = "DROP TABLE guides;";
            stmt.execute(dropOldTableSQL);

            // Step 4: Rename the new table to the original name
            String renameTableSQL = "ALTER TABLE guides_new RENAME TO guides;";
            stmt.execute(renameTableSQL);

            System.out.println("Guide table schema updated successfully!");

        } catch (SQLException e) {
            System.err.println("Error updating Guide table schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
