package me.ancastanoev;

import me.ancastanoev.database.DatabaseConnection;
import me.ancastanoev.io.InputException;

import java.io.IOException;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) throws InputException, IOException, ClassNotFoundException {


                Connection conn = DatabaseConnection.connect();
                if (conn != null) {
                    System.out.println("Database connection successful.");
                } else {
                    System.err.println("Database connection failed.");
                }


        RouteManager.setPaths(Application.getPaths());
        // RouteManager.setOutputDevice(Application.getOutputDevice());
        ClimberManager.setClimbers(Application.getClimbers());
        ClimberManager.setOutputDevice(Application.getOutputDevice());
        Helper.setOutputDevice(Application.getOutputDevice());

        if (args.length == 0) {
            System.out.println("Error: No arguments provided. Please specify 'user', 'admin', or 'help'.");
            return;
        }

        String mode = args[0].toLowerCase();

        switch (mode) {
            case "user":
                System.out.println("Running application in User mode...");
                try {
                    Application.runAsUser();
                } catch (ApplicationException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "admin":
                System.out.println("Running application in Admin mode...");
                Application.runAsAdmin();
                break;

            case "help":
                System.out.println(
                        "\n--- me.ancastanoev.Application.Application Help ---\n\n" +
                                "This application is designed to support climbers, guides, and administrators in managing mountain expeditions. " +
                                "It has three main modes:\n\n" +

                                "1. **User Mode**\n" +
                                "   Run with the argument `user` to enter this mode.\n" +
                                "   - Allows registered users to log in and access options for planning expeditions and viewing information.\n" +
                                "   - Users can create new accounts or log in with existing ones.\n" +
                                "   - Features include:\n" +
                                "     • Planning and joining expeditions based on skill level and preferred mountains\n" +
                                "     • Viewing groups of climbers organized by experience\n" +
                                "     • Checking available expeditions and joining them\n\n" +

                                "2. **Admin Mode**\n" +
                                "   Run with the argument `admin` to enter this mode.\n" +
                                "   - Provides full access to manage climbers, guides, mountains, routes, and expeditions.\n" +
                                "   - Admin functions include:\n" +
                                "     • Adding, updating, or removing data for climbers, mountains, guides, and routes\n" +
                                "     • Sorting climbers and paths by experience, difficulty, and other criteria\n" +
                                "     • Generating reports on climber progress and automating experience level updates\n" +
                                "     • Organizing expeditions and assigning guides based on mountain requirements\n\n" +

                                "3. **Help Mode**\n" +
                                "   Run with the argument `help` to display this help message.\n" +
                                "   - This message provides information about each mode and usage instructions.\n\n" +

                                "Usage Examples:\n" +
                                "   java me.ancastanoev.Main user    - Runs the application in User mode\n" +
                                "   java me.ancastanoev.Main admin   - Runs the application in Admin mode\n" +
                                "   java me.ancastanoev.Main help    - Displays this help message\n" +
                                "-------------------------\n"
                );
                break;

            default:
                throw new IllegalArgumentException("Error: Invalid argument. Please specify 'user', 'admin', or 'help'.");
        }
    }
}
