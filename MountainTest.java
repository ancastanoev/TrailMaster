package me.ancastanoev;

import org.junit.Test;
import static org.junit.Assert.*;

public class MountainTest {

    @Test
    public void testConstructorValidData() {
        Climber leadClimber = new Climber("Alice", "Smith", "Expert", "alice@example.com");
        Mountain mountain = new Mountain("Everest", leadClimber, new Expedition[0]);

        assertEquals("Everest", mountain.getName());
        assertEquals(leadClimber, mountain.getLeadClimber());
        assertNotNull(mountain.getExpeditions());
        assertTrue(mountain.getExpeditions().isEmpty());
    }

    @Test
    public void testConstructorInvalidName() {
        Climber leadClimber = new Climber("Alice", "Smith", "Expert", "alice@example.com");
        try {
            new Mountain("!!InvalidName", leadClimber, new Expedition[0]);
            fail("Expected IllegalArgumentException to be thrown due to invalid mountain name.");
        } catch (IllegalArgumentException e) {
            assertEquals("Mountain name cannot contain special characters.", e.getMessage());
        }
    }

    @Test
    public void testAssignExpedition() {
        Climber leadClimber = new Climber("Alice", "Smith", "Expert", "alice@example.com");
        Mountain mountain = new Mountain("Everest", leadClimber, new Expedition[0]);
        // Ensure the route string matches the expected format
        Expedition expedition = new Expedition("2025-01-01", "2025-01-02", "Planned", "Difficult", "Route A,Easy,Mountain X");

        mountain.addExpedition(expedition);
        assertTrue(mountain.getExpeditions().contains(expedition));
    }
}
