package me.ancastanoev.database;

import me.ancastanoev.Mountain;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MountainDAOTest {

    private MountainDAO mountainDAO;

    @Before
    public void setUp() {
        mountainDAO = new MountainDAO();
        // FileHandler.setRole("admin");
    }

    @Test
    public void testConstructor() {
        assertNotNull("MountainDAO instance should not be null", mountainDAO);
    }

    @Test
    public void testAddAndGetMountain() {
        Mountain testMountain = new Mountain("Test Mountain");

        // Add mountain
        mountainDAO.addMountain("admin", testMountain);

        // Retrieve all mountains
        List<Mountain> mountains = mountainDAO.getAllMountains("admin");
        assertNotNull("Mountains list should not be null", mountains);

        // Check presence
        boolean found = mountains.stream().anyMatch(
                m -> m.getName().equals("Test Mountain")
        );
        assertTrue("Newly added mountain should be present", found);
    }

    @Test
    public void testDeleteMountain() {
        // Assume we have a mountain named "Test Mountain"
        mountainDAO.deleteMountain("admin", "Test Mountain");

        // Ensure it's removed
        List<Mountain> mountains = mountainDAO.getAllMountains("admin");
        boolean found = mountains.stream().anyMatch(
                m -> m.getName().equals("Test Mountain")
        );
        assertFalse("Mountain should be deleted", found);
    }
}
