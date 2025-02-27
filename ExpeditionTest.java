package me.ancastanoev;

import me.ancastanoev.io.InputException;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class ExpeditionTest {

    @Test
    public void testConstructorValidData() {
        // Create a Route object with correct format
        Route route = new Route("Route A", "Easy", "Mountain X");

        // Instantiate Expedition using the constructor that accepts a Route object
        Expedition expedition = new Expedition("2025-01-01", "2025-01-02", "Planned", "Easy", route);

        assertEquals("2025-01-01", expedition.getStartDate());
        assertEquals("2025-01-02", expedition.getEndDate());
        assertEquals("Planned", expedition.getOutcome());
        assertEquals("Easy", expedition.getDifficultyLevel());
        assertEquals("Route A", expedition.getRoute().getName());
        assertEquals("Mountain X", expedition.getRoute().getMountainName());
    }

    @Test
    public void testConstructorInvalidDate() {
        try {
            // This should throw an IllegalArgumentException because endDate is before startDate
            new Expedition("2025-01-02", "2025-01-01", "Planned", "Easy", "Route A,Easy,Mountain X");
            fail("Expected IllegalArgumentException to be thrown due to invalid dates");
        } catch (IllegalArgumentException e) {
            assertEquals("End date must be after start date", e.getMessage());
        }
    }

    @Test
    public void testAddClimber() {
        // Create a Route object
        Route route = new Route("Route A", "Easy", "Mountain X");

        Expedition expedition = new Expedition("2025-01-01", "2025-01-02", "Planned", "Easy", route);
        Climber climber = new Climber("John", "Doe", "Beginner", "john.doe@example.com");

        expedition.addClimber(climber);

        assertTrue(expedition.getClimbers().contains(climber));
    }

    @Test
    public void testDisplayInfo() {
        // Create a Route object
        Route route = new Route("Route A", "Easy", "Mountain X");

        Expedition expedition = new Expedition("2025-01-01", "2025-01-02", "Planned", "Easy", route);
        String info = expedition.displayInfo();

        assertTrue(info.contains("Expedition Start Date: 2025-01-01"));
        assertTrue(info.contains("Expedition End Date: 2025-01-02"));
        assertTrue(info.contains("Difficulty Level: Easy"));
        assertTrue(info.contains("Outcome: Planned"));
        assertTrue(info.contains("Route: Route A on Mountain: Mountain X"));
        assertTrue(info.contains("Climbers:"));
    }
}
