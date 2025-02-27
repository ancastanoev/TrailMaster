package me.ancastanoev.database;

import me.ancastanoev.Expedition;
import me.ancastanoev.Route;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ExpeditionDAOTest {

    private ExpeditionDAO expeditionDAO;

    @Before
    public void setUp() {
        expeditionDAO = new ExpeditionDAO();
        // Possibly set role to admin
        // FileHandler.setRole("admin");
    }

    @Test
    public void testConstructor() {
        assertNotNull("ExpeditionDAO instance should not be null", expeditionDAO);
    }

    @Test
    public void testAddAndGetExpedition() {
        // Create a test expedition
        Route dummyRoute = new Route("DummyRoute", "Easy", "DummyMountain");
        Expedition expedition = new Expedition("2025-01-01", "2025-01-05", "Easy", dummyRoute);

        // Add it
        boolean added = expeditionDAO.addExpedition("admin", expedition);
        assertTrue("Expedition should be added successfully", added);

        // Retrieve
        List<Expedition> expeditions = expeditionDAO.getAllExpeditions("admin");
        assertNotNull("Expeditions list should not be null", expeditions);

        // Check presence
        boolean found = expeditions.stream().anyMatch(
                e -> e.getRoute().getName().equals("DummyRoute")
        );
        assertTrue("Test expedition should be in the DB", found);
    }


}
