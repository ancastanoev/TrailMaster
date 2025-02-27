package me.ancastanoev.database;

import me.ancastanoev.Guide;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GuideDAOTest {

    private GuideDAO guideDAO;

    @Before
    public void setUp() {
        guideDAO = new GuideDAO();
        // FileHandler.setRole("admin");
    }

    @Test
    public void testConstructor() {
        assertNotNull("GuideDAO instance should not be null", guideDAO);
    }

    @Test
    public void testAddAndGetGuide() {
        Guide testGuide = new Guide("Test Guide", "Beginner");

        // Add guide (admin only)
        guideDAO.addGuide("admin", testGuide);

        // Retrieve
        List<Guide> guides = guideDAO.getAllGuides("admin");
        assertNotNull("Guides list should not be null", guides);

        // Check presence
        boolean found = guides.stream().anyMatch(
                g -> g.getName().equals("Test Guide")
        );
        assertTrue("Newly added guide should be present", found);
    }

    @Test
    public void testUpdateGuide() {
        // Let's assume we know a guide ID (e.g., 1)
        Guide updatedGuide = new Guide("Updated Guide Name", "Intermediate");
        guideDAO.updateGuide("admin", 1, updatedGuide);

        // Retrieve
        Guide guideFromDB = guideDAO.getGuideById("admin", 1);
        assertNotNull("Guide with ID=1 should exist", guideFromDB);
        assertEquals("Name should be updated", "Updated Guide Name", guideFromDB.getName());
        assertEquals("Experience should be Intermediate", "Intermediate", guideFromDB.getExperienceLevel());
    }
}
