package me.ancastanoev;

import me.ancastanoev.io.InputException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class RouteManagerTest {

    @Before
    public void setUp() {
        RouteManager.clearPaths(); // Ensure a clean state before each test
    }

    @Test
    public void testAddPath() {
        try {
            RouteManager.addPath("Trail B", "Difficult", "Mountain B");
        } catch (InputException e) {
            throw new RuntimeException(e);
        }
        assertTrue(RouteManager.getPaths().stream()
                .anyMatch(route -> route.getName().equalsIgnoreCase("Trail B")));
    }

    @Test
    public void testSortRoutesByDifficultyAndName() {
        try {
            RouteManager.addPath("Trail B", "Difficult", "Mountain B");
            RouteManager.addPath("Trail A", "Easy", "Mountain A");
        } catch (InputException e) {
            throw new RuntimeException(e);
        }

        List<Route> sortedRoutes = RouteManager.sortRoutesByDifficultyAndName();
        assertFalse("Sorted routes should not be empty.", sortedRoutes.isEmpty());
        assertEquals("Trail A", sortedRoutes.get(0).getName());
        assertEquals("Trail B", sortedRoutes.get(1).getName());
    }

    @Test
    public void testAddPathWithInvalidDifficulty() {
        try {
            RouteManager.addPath("Trail C", "Extreme", "Mountain C");
            fail("Expected InputException due to invalid difficulty level.");
        } catch (InputException e) {
            assertEquals("Invalid difficulty level. Must be 'Easy', 'Moderate', or 'Difficult'.", e.getMessage());
        }
    }

    @Test
    public void testAddPathWithEmptyName() {
        try {
            RouteManager.addPath("", "Easy", "Mountain D");
            fail("Expected InputException due to empty route name.");
        } catch (InputException e) {
            assertEquals("Route name cannot be null or empty.", e.getMessage());
        }
    }

    @Test
    public void testSortRoutesWithSameDifficulty() {
        try {
            RouteManager.addPath("Trail B", "Moderate", "Mountain B");
            RouteManager.addPath("Trail A", "Moderate", "Mountain A");
        } catch (InputException e) {
            throw new RuntimeException(e);
        }

        List<Route> sortedRoutes = RouteManager.sortRoutesByDifficultyAndName();
        assertEquals("Trail A", sortedRoutes.get(0).getName());
        assertEquals("Trail B", sortedRoutes.get(1).getName());
    }
}
