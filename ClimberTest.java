package me.ancastanoev;
import me.ancastanoev.io.InputException;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class ClimberTest {
    @Test
    public void testConstructor() {
        Climber climber = new Climber("John", "Doe", "Beginner", "john.doe@example.com");
        assertEquals("John", climber.getFirstName());
        assertEquals("Doe", climber.getLastName());
        assertEquals("Beginner", climber.getExperienceLevel());
        assertEquals("john.doe@example.com", climber.getContactInfo());
    }



    @Test
    public void testInvalidExperienceLevel() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Climber("John", "Doe", "InvalidLevel", "john.doe@example.com");
        });
        assertTrue(exception.getMessage().contains("Invalid experience level"));
    }
}
