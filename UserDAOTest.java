package me.ancastanoev.database;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserDAOTest {

    private UserDAO userDAO;

    @Before
    public void setUp() {
        userDAO = new UserDAO();
        userDAO.createUsersTable("admin");
    }

    @Test
    public void testConstructor() {
        assertNotNull("UserDAO instance should not be null", userDAO);
    }

    @Test
    public void testCreateAndAuthenticateUser() {
        boolean created = userDAO.createUser("testuser", "testpass", "User");
        assertTrue("User should be created successfully", created);

        boolean authenticated = userDAO.authenticateUser("testuser", "testpass");
        assertTrue("Authentication should succeed", authenticated);
    }

    @Test
    public void testGetUserRole() {
        userDAO.createUser("adminuser", "adminpass", "Admin");

        String role = userDAO.getUserRole("adminuser");
        assertEquals("Role should be 'Admin'", "Admin", role);
    }
}
