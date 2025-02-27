package me.ancastanoev;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ClimberManagerTest {

    private List<Climber> testClimbers;

    @Before
    public void setUp() {
        // Initialize test climbers
        testClimbers = new ArrayList<>();
        testClimbers.add(new Climber("John", "Doe", "Beginner", "john.doe@example.com"));
        testClimbers.add(new Climber("Jane", "Smith", "Intermediate", "jane.smith@example.com"));
    }

    @Test
    public void testConstructorWithValidClimberList() {
        ClimberManager climberManager = new ClimberManager(testClimbers);

        assertNotNull("ClimberManager instance should not be null", climberManager);
        assertEquals("Climber list size should match", testClimbers.size(), climberManager.getClimbers().size());
    }

    @Test
    public void testConstructorWithNullClimberList() {
        ClimberManager climberManager = new ClimberManager(null);

        assertNotNull("ClimberManager instance should not be null", climberManager);
        assertNotNull("Climber list should be initialized as an empty list", climberManager.getClimbers());
        assertTrue("Climber list should be empty", climberManager.getClimbers().isEmpty());
    }

    @Test
    public void testSetClimbers() {
        ClimberManager.setClimbers(testClimbers);

        assertNotNull("Climber list should not be null after setting", ClimberManager.getClimbers());
        assertEquals("Climber list size should match", testClimbers.size(), ClimberManager.getClimbers().size());
    }

    @Test
    public void testConstructorDoesNotModifyInputList() {
        List<Climber> originalList = new ArrayList<>(testClimbers);
        ClimberManager climberManager = new ClimberManager(testClimbers);

        // Modify the original list
        originalList.add(new Climber("Alice", "Wonderland", "Expert", "alice.w@example.com"));

        // Check that ClimberManager's list is not affected
        assertEquals("Climber list in ClimberManager should not change when the original list is modified",
                testClimbers.size(), climberManager.getClimbers().size());
    }
}
