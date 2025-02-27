package me.ancastanoev;

import me.ancastanoev.database.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FileHandlerTest {

    @Before
    public void setUp() {
        // Set the role to admin for testing purposes
        FileHandler.setRole("admin");
    }

    @Test
    public void testLoadClimbers() {
        List<Climber> climbers = FileHandler.loadClimbers(null); // FilePath is not used in DB implementation
        assertNotNull("Climbers list should not be null", climbers);
        // Assuming the database is pre-populated for testing, or mock the DAO for unit testing
        assertFalse("Climbers list should not be empty", climbers.isEmpty());
    }

    @Test
    public void testSaveClimbers() {
        Climber testClimber = new Climber("Test", "Climber", "Beginner", "test.climber@example.com");
        List<Climber> climbers = List.of(testClimber);

        // Save the climber to the database
        FileHandler.saveClimbers(climbers);

        // Reload climbers from the database and check if the new climber is present
        List<Climber> loadedClimbers = FileHandler.loadClimbers(null);
        assertTrue("Climber list should contain the saved climber",
                loadedClimbers.stream().anyMatch(c -> c.getFirstName().equals("Test") && c.getLastName().equals("Climber")));
    }

    @Test
    public void testLoadRoutes() {
        List<Route> routes = FileHandler.loadRoutes(null);
        assertNotNull("Routes list should not be null", routes);
        assertFalse("Routes list should not be empty", routes.isEmpty());
    }

    @Test
    public void testSaveRoutes() {
        Route testRoute = new Route("Test Route", "Easy", "Test Mountain");
        List<Route> routes = List.of(testRoute);

        // Save the route to the database
        FileHandler.saveRoutes(routes);

        // Reload routes from the database and check if the new route is present
        List<Route> loadedRoutes = FileHandler.loadRoutes(null);
        assertTrue("Routes list should contain the saved route",
                loadedRoutes.stream().anyMatch(r -> r.getName().equals("Test Route") && r.getMountainName().equals("Test Mountain")));
    }

    @Test
    public void testConstructorInteraction() {
        // Test DAO instances and ensure they are non-null
        ClimberDAO climberDAO = new ClimberDAO();
        assertNotNull("ClimberDAO should be instantiated", climberDAO);

        RouteDAO routeDAO = new RouteDAO();
        assertNotNull("RouteDAO should be instantiated", routeDAO);

        GuideDAO guideDAO = new GuideDAO();
        assertNotNull("GuideDAO should be instantiated", guideDAO);

        ExpeditionDAO expeditionDAO = new ExpeditionDAO();
        assertNotNull("ExpeditionDAO should be instantiated", expeditionDAO);

        MountainDAO mountainDAO = new MountainDAO();
        assertNotNull("MountainDAO should be instantiated", mountainDAO);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAppendUnsupportedOperation() {
        // This should throw an exception as append is no longer supported
        FileHandler.append("Sample Data", "sample.txt");
    }
}
