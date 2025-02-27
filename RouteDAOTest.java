package me.ancastanoev.database;

import me.ancastanoev.Route;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RouteDAOTest {

    private RouteDAO routeDAO;

    @Before
    public void setUp() {
        routeDAO = new RouteDAO();
        // FileHandler.setRole("admin");
    }

    @Test
    public void testConstructor() {
        assertNotNull("RouteDAO instance should not be null", routeDAO);
    }

    @Test
    public void testAddAndGetRoute() {
        Route testRoute = new Route("Test Route", "Easy", "Test Mountain");
        routeDAO.addRoute("admin", testRoute);

        List<Route> routes = routeDAO.getAllRoutes("admin");
        assertNotNull("Routes list should not be null", routes);

        boolean found = routes.stream().anyMatch(
                r -> r.getName().equals("Test Route") && r.getMountainName().equals("Test Mountain")
        );
        assertTrue("Newly added route should be found", found);
    }



}
