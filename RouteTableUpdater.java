package me.ancastanoev.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class RouteTableUpdater {

    public static void updateRoutesTableSchema() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            // Step 1: Delete all data only before altering the schema
           // deleteAllData();

            // Step 2: Add missing column "mountain_name" if it doesn't exist
            String addMountainNameColumn = "ALTER TABLE routes ADD COLUMN mountain_name TEXT";
            try {
                stmt.execute(addMountainNameColumn);
                System.out.println("'mountain_name' column added to 'routes' table.");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    throw e; // Rethrow if it's a different error
                }
                System.out.println("'mountain_name' column already exists.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteAllData() {
        String deleteSQL = "DELETE FROM routes";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(deleteSQL);
            System.out.println("All data deleted from 'routes' table.");
        } catch (SQLException e) {
            System.out.println("Error deleting data from 'routes' table: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
