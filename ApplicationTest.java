package me.ancastanoev;
import me.ancastanoev.io.InputException;import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

public class ApplicationTest {

    @Test
    public void testLoadData() throws IOException, ClassNotFoundException, InputException {
        Application.loadData(false);
        assertNotNull(Application.getClimbers());
        assertNotNull(Application.getPaths());
        assertNotNull(Application.getGuides());
        assertNotNull(Application.getMountains());
    }

    @Test
    public void testIsRouteAccessibleForExperience() {
        Route easyRoute = new Route("Trail A", "Easy", "Mountain A");
        Route difficultRoute = new Route("Trail B", "Difficult", "Mountain B");

        assertTrue(Application.isRouteAccessibleForExperience(easyRoute, "Beginner"));
        assertFalse(Application.isRouteAccessibleForExperience(difficultRoute, "Beginner"));
    }
}
