package me.ancastanoev;

import org.junit.Test;
import static org.junit.Assert.*;

public class HelperTest {

    @Test
    public void testIsEndDateAfterStartDate_ValidDates() {
        assertTrue(Helper.isEndDateAfterStartDate("2025-01-01", "2025-01-02"));
        assertFalse(Helper.isEndDateAfterStartDate("2025-01-02", "2025-01-01"));
        assertFalse(Helper.isEndDateAfterStartDate("2025-01-01", "2025-01-01"));
    }

    @Test
    public void testIsEndDateAfterStartDate_InvalidStartDate() {
        try {
            Helper.isEndDateAfterStartDate("2025-13-01", "2025-01-02"); // Invalid month
            fail("Expected IllegalArgumentException due to invalid start date format");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid date format. Expected format: yyyy-MM-dd", e.getMessage());
        }
    }

    @Test
    public void testIsEndDateAfterStartDate_InvalidEndDate() {
        try {
            Helper.isEndDateAfterStartDate("2025-01-01", "2025-01-32"); // Invalid day
            fail("Expected IllegalArgumentException due to invalid end date format");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid date format. Expected format: yyyy-MM-dd", e.getMessage());
        }
    }

    @Test
    public void testIsEndDateAfterStartDate_InvalidDateFormat() {
        try {
            Helper.isEndDateAfterStartDate("01-01-2025", "02-01-2025"); // Wrong format
            fail("Expected IllegalArgumentException due to invalid date format");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid date format. Expected format: yyyy-MM-dd", e.getMessage());
        }
    }
}
