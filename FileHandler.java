package me.ancastanoev;

import me.ancastanoev.database.*;
import java.util.List;

public class FileHandler {

    // DAO instances for interacting with the database
    private static final ClimberDAO climberDAO = new ClimberDAO();
    private static final RouteDAO routeDAO = new RouteDAO();
    private static final GuideDAO guideDAO = new GuideDAO();
    private static final ExpeditionDAO expeditionDAO = new ExpeditionDAO();
    private static final MountainDAO mountainDAO = new MountainDAO();

    // Role for controlling access
    private static String currentRole;

    // Method to set the role
    public static void setRole(String role) {
        currentRole = role;
    }

    // Load Climbers from the database
    public static List<Climber> loadClimbers(String filePath) {
        System.out.println("Loading climbers from the database.");
        return climberDAO.getAllClimbers(currentRole);
    }

    // Save Climbers to the database
    public static void saveClimbers(List<Climber> climbers) {
        if (!currentRole.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can save climbers.");
            return;
        }
        System.out.println("Saving climbers to the database.");
        for (Climber climber : climbers) {
            climberDAO.addClimber(currentRole, climber);
        }
    }

    // Load Routes from the database
    public static List<Route> loadRoutes(String filePath) {
        System.out.println("Loading routes from the database.");
        return routeDAO.getAllRoutes(currentRole);
    }

    // Save Routes to the database
    public static void saveRoutes(List<Route> routes) {
        if (!currentRole.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can save routes.");
            return;
        }
        System.out.println("Saving routes to the database.");
        routes.forEach(route -> routeDAO.addRoute(currentRole, route));
    }

    // Load Guides from the database
    public static List<Guide> loadGuides(String filePath) {
        System.out.println("Loading guides from the database.");
        return guideDAO.getAllGuides(currentRole);
    }

    // Save Guides to the database
    public static void saveGuides(List<Guide> guides) {
        if (!currentRole.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can save guides.");
            return;
        }
        System.out.println("Saving guides to the database.");
        guides.forEach(guide -> guideDAO.addGuide(currentRole, guide));
    }

    // Load Expeditions from the database
    public static List<Expedition> loadExpeditions(String filePath) {
        System.out.println("Loading expeditions from the database.");
        return expeditionDAO.getAllExpeditions(currentRole);
    }

    // Save Expeditions to the database
    public static void saveExpeditions(List<Expedition> expeditions) {
        if (!currentRole.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can save expeditions.");
            return;
        }
        System.out.println("Saving expeditions to the database.");
        expeditions.forEach(expedition -> expeditionDAO.addExpedition(currentRole, expedition));
    }

    // Load Mountains from the database
    public static List<Mountain> loadMountains(String filePath) {
        System.out.println("Loading mountains from the database.");
        return mountainDAO.getAllMountains(currentRole);
    }

    // Save Mountains to the database
    public static void saveMountains(List<Mountain> mountains) {
        if (!currentRole.equalsIgnoreCase("admin")) {
            System.out.println("Permission denied: Only admins can save mountains.");
            return;
        }
        System.out.println("Saving mountains to the database.");
        mountains.forEach(mountain -> mountainDAO.addMountain(currentRole, mountain));
    }

    // Append data operation is no longer supported
    public static void append(String data, String filePath) {
        System.out.println("Append operation is no longer supported with the database.");
        throw new UnsupportedOperationException("Append is not supported with database integration.");
    }
}
