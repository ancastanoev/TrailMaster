package me.ancastanoev.database;

import me.ancastanoev.Climber;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ClimberDAOTest {

    private ClimberDAO climberDAO;

    @Before
    public void setUp() {
        // Instantiate the DAO
        climberDAO = new ClimberDAO();
        // Optionally: set a test role or ensure "admin"
        // (depending on how you handle roles in your tests)
        // FileHandler.setRole("admin"); // or similar if needed
    }

    @Test
    public void testConstructor() {
        assertNotNull("ClimberDAO instance should not be null", climberDAO);
    }

    @Test
    public void testAddAndGetClimber() {
        // Create a test climber
        Climber testClimber = new Climber("Test", "Climber", "Beginner", "test.climber@example.com");

        // Add the climber
        climberDAO.addClimber("admin", testClimber);

        // Retrieve all climbers
        List<Climber> climbers = climberDAO.getAllClimbers("admin");
        assertNotNull("Climbers list should not be null", climbers);

        // Check if the test climber is present
        boolean found = climbers.stream().anyMatch(
                c -> c.getFirstName().equals("Test") && c.getLastName().equals("Climber")
        );
        assertTrue("Test climber should be found in the database", found);
    }

    @Test
    public void testUpdateExperienceLevel() {
        // Assume we've already added a "Test Climber"
        // Update the experience level
        boolean updated = climberDAO.updateExperienceLevel("admin", "Test", "Climber", "Expert");
        assertTrue("Experience level should have been updated", updated);

        // Retrieve all climbers and confirm the updated level
        List<Climber> climbers = climberDAO.getAllClimbers("admin");
        Climber updatedClimber = climbers.stream()
                .filter(c -> c.getFirstName().equals("Test") && c.getLastName().equals("Climber"))
                .findFirst().orElse(null);

        assertNotNull("Updated climber should still exist", updatedClimber);
        assertEquals("Experience level should now be Expert", "Expert", updatedClimber.getExperienceLevel());
    }
}
