package me.ancastanoev;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
public class ApplicationException extends Exception {

    static final List<String> EXPERIENCE_LEVELS = List.of("Beginner", "Intermediate", "Expert");
    private static final List<String> DIFFICULTY_LEVELS = List.of("Easy", "Moderate", "Difficult");


    public ApplicationException(String message) {
        super(message);
    }


    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ApplicationException climberAlreadyExists(String firstName, String lastName) {
        return new ApplicationException("Climber " + firstName + " " + lastName + " already exists.");
    }

    public static ApplicationException invalidInput(String inputName) {
        return new ApplicationException("Invalid input for " + inputName + ". Please try again.");
    }

    public static ApplicationException fileOperationError(String fileName, Throwable cause) {
        return new ApplicationException("Error loading or saving file: " + fileName, cause);
    }

    //  validation methods for experience, difficulty, and date

    public static void validateExperienceLevel(String experience) throws ApplicationException {
        if (!EXPERIENCE_LEVELS.contains(experience)) {
            throw new ApplicationException("Invalid experience level. Must be one of: Beginner, Intermediate, Expert.");
        }
    }

    public static void validateDifficultyLevel(String difficulty) throws ApplicationException {
        if (!DIFFICULTY_LEVELS.contains(difficulty)) {
            throw new ApplicationException("Invalid difficulty level. Must be one of: Easy, Moderate, Difficult.");
        }
    }

    public static void validateDate(String date) throws ApplicationException {
        //regex to validate the date format YYYY/MM/DD
        String regex = "^\\d{4}/(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])$";

        if (!date.matches(regex)) {
            throw new ApplicationException("Invalid date format. Please use yyyy/mm/dd.");
        }

        int year = Integer.parseInt(date.substring(0, 4));

        // we choose the year range that is accepted for planing an expedition
        int minYear = 2024;
        int maxYear = 2028;

        if (year < minYear || year > maxYear) {
            throw new ApplicationException("Year must be between " + minYear + " and " + maxYear + ".");
        }
        //further validations
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate parsedDate = LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new ApplicationException("Invalid date. Please ensure the day is correct for the given month.");
        }
    }
}