package me.ancastanoev;

import org.junit.Test;
import static org.junit.Assert.*;

public class GuideTest {

    @Test
    public void testConstructorValidData() {
        Guide guide = new Guide("Maria", "Intermediate");
        assertEquals("Maria", guide.getName());
        assertEquals("Intermediate", guide.getExperienceLevel());
    }

    @Test
    public void testConstructorInvalidExperienceLevel() {
        try {
            new Guide("Maria", "InvalidLevel");
            fail("Expected IllegalArgumentException to be thrown due to invalid experience level");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid experience level", e.getMessage());
        }
    }

    @Test
    public void testToText() {
        Guide guide = new Guide("Maria", "Intermediate");
        assertEquals("Maria,Intermediate", guide.toText());
    }

    @Test
    public void testFromTextValid() {
        Guide guide = Guide.fromText("Maria,Intermediate");
        assertEquals("Maria", guide.getName());
        assertEquals("Intermediate", guide.getExperienceLevel());
    }

    @Test
    public void testFromTextInvalid() {
        try {
            Guide.fromText("InvalidData");
            fail("Expected IllegalArgumentException to be thrown due to invalid guide data format");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid guide data format. Expected format: 'name,experienceLevel'", e.getMessage());
        }
    }
}
