package me.ancastanoev.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableEditor {

    // Method to add a column to a table
    public static void addColumn(String tableName, String columnName, String columnType) {
        String alterTableSQL = String.format("ALTER TABLE %s ADD COLUMN %s %s;", tableName, columnName, columnType);

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(alterTableSQL);
            System.out.println("Column " + columnName + " added successfully to table " + tableName);
        } catch (SQLException e) {
            System.err.println("Error adding column to table: " + e.getMessage());
        }
    }

    // Method to drop a table
    public static void dropTable(String tableName) {
        String dropTableSQL = String.format("DROP TABLE IF EXISTS %s;", tableName);

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(dropTableSQL);
            System.out.println("Table " + tableName + " dropped successfully.");
        } catch (SQLException e) {
            System.err.println("Error dropping table: " + e.getMessage());
        }
    }

    // Method to recreate a table with a new structure
    public static void recreateTable(String tableName, String createTableSQL) {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            // Drop the table if it exists
            String dropTableSQL = String.format("DROP TABLE IF EXISTS %s;", tableName);
            stmt.execute(dropTableSQL);
            System.out.println("Table " + tableName + " dropped successfully.");

            // Create the table with the new structure
            stmt.execute(createTableSQL);
            System.out.println("Table " + tableName + " recreated successfully with the new structure.");
        } catch (SQLException e) {
            System.err.println("Error recreating table: " + e.getMessage());
        }
    }
}
