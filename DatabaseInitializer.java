package me.ancastanoev.database;

public class DatabaseInitializer {

    public static void createTables(String role) {
        try {
            // Enforce role-based permissions
            if (!"admin".equalsIgnoreCase(role)) {
                System.out.println("Permission denied: Only admin users can create database tables.");
                return;
            }

            // Create tables using DAO classes with role-based access
            new UserDAO().createUsersTable(role); // Pass role to createUsersTable
            new ClimberDAO().createClimbersTable(role); // Pass role to createClimbersTable
            new RouteDAO().createRoutesTable(role); // Pass role to createRoutesTable
            new GuideDAO().createGuidesTable(role); // Pass role to createGuidesTable
            new MountainDAO().createMountainsTable(role); // Pass role to createMountainsTable
            new ExpeditionDAO().createExpeditionsTable(role); // Pass role to createExpeditionsTable

            System.out.println("All tables created successfully!");
        } catch (Exception e) {
            System.out.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
