package me.ancastanoev;

import org.junit.Test;
import static org.junit.Assert.*;

public class RouteTest {

    @Test
    public void testFromTextValid() {
        String validRouteData = "TrailX,Hard,MountainX";
        Route route = Route.fromText(validRouteData);
        assertNotNull(route);
        assertEquals("TrailX", route.getName());
        assertEquals("Hard", route.getDifficultyLevel());
        assertEquals("MountainX", route.getMountainName());
    }

    @Test
    public void testFromTextInvalid() {
        String invalidRouteData = "TrailX,Hard"; // Missing mountain name
        try {
            Route.fromText(invalidRouteData);
            fail("Expected IllegalArgumentException to be thrown due to invalid route data format.");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid Route data format. Expected format: 'name,difficultyLevel,mountainName'", e.getMessage());
        }
    }
}
