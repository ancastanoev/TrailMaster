package me.ancastanoev;

import me.ancastanoev.io.InputException;

import java.util.*;
import java.util.stream.Collectors;

public class RouteManager {
    private static List<Route> paths = new ArrayList<>();

    /**
     * Adds a new route to the RouteManager.
     *
     * @param name         The name of the route.
     * @param difficulty   The difficulty level of the route.
     * @param mountainName The name of the mountain the route belongs to.
     * @throws InputException if any of the inputs are invalid.
     */
    public static void addPath(String name, String difficulty, String mountainName) throws InputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InputException("Route name cannot be null or empty.");
        }
        if (difficulty == null || difficulty.trim().isEmpty()) {
            throw new InputException("Difficulty level cannot be null or empty.");
        }
        if (mountainName == null || mountainName.trim().isEmpty()) {
            throw new InputException("Mountain name cannot be null or empty.");
        }

        // Optional: Validate difficulty levels
        List<String> validDifficulties = List.of("Easy", "Moderate", "Difficult");
        if (!validDifficulties.contains(difficulty)) {
            throw new InputException("Invalid difficulty level. Must be 'Easy', 'Moderate', or 'Difficult'.");
        }

        Route newRoute = new Route(name, difficulty, mountainName);
        paths.add(newRoute);
    }

    /**
     * Retrieves all routes.
     *
     * @return A list of all routes.
     */
    public static List<Route> getPaths() {
        return new ArrayList<>(paths); // Return a copy to prevent external modification
    }

    /**
     * Sorts routes first by difficulty and then by name.
     *
     * @return A sorted list of routes.
     */
    public static List<Route> sortRoutesByDifficultyAndName() {
        // Define the order of difficulties
        List<String> difficultyOrder = List.of("Easy", "Moderate", "Difficult");

        return paths.stream()
                .sorted(Comparator
                        .comparing((Route r) -> difficultyOrder.indexOf(r.getDifficulty()))
                        .thenComparing(Route::getName))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all routes associated with the given mountain.
     *
     * @param mountain The mountain for which to load routes.
     * @return A list of routes belonging to the specified mountain.
     * @throws IllegalArgumentException if the mountain is null.
     */
    public static List<Route> loadRoutesForMountain(Mountain mountain) {
        if (mountain == null) {
            throw new IllegalArgumentException("Mountain cannot be null.");
        }
        String mountainName = mountain.getName();
        return paths.stream()
                .filter(route -> route.getMountainName().equalsIgnoreCase(mountainName))
                .collect(Collectors.toList());
    }

    /**
     * Clears all routes from the RouteManager.
     * Useful for resetting state between tests.
     */
    public static void clearPaths() {
        paths.clear();
    }

    /**
     * Allows the user to select a route from the list of accessible routes.
     *
     * @param accessibleRoutes The list of routes available for selection.
     * @param scanner          The Scanner object to read user input.
     * @return The selected Route object, or null if no routes are available.
     */
    public static Route getRouteSelection(List<Route> accessibleRoutes, Scanner scanner) {
        if (accessibleRoutes == null || accessibleRoutes.isEmpty()) {
            System.out.println("No accessible routes available for selection.");
            return null;
        }

        while (true) {
            System.out.println("\nAvailable Routes:");
            for (int i = 0; i < accessibleRoutes.size(); i++) {
                Route route = accessibleRoutes.get(i);
                System.out.printf("%d. %s - %s%n", i + 1, route.getName(), route.getDifficulty());
            }
            System.out.printf("%d. Exit Selection%n", accessibleRoutes.size() + 1);
            System.out.print("Please enter the number corresponding to your chosen route: ");

            int choice = -1;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
                continue;
            }

            if (choice == accessibleRoutes.size() + 1) {
                System.out.println("Exiting route selection.");
                return null;
            }

            if (choice < 1 || choice > accessibleRoutes.size() + 1) {
                System.out.println("Invalid choice. Please select a number from the list.");
                continue;
            }

            // Valid selection
            Route selectedRoute = accessibleRoutes.get(choice - 1);
            System.out.printf("You have selected: %s - %s%n", selectedRoute.getName(), selectedRoute.getDifficulty());
            return selectedRoute;
        }
    }

    public static void setPaths(List<Route> paths) {
        RouteManager.paths=paths;
    }
}
