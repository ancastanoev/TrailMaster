package me.ancastanoev;

import me.ancastanoev.io.InputDevice;
import me.ancastanoev.io.InputException;
import me.ancastanoev.io.OutputDevice;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Helper {
    private static final List<String> EXPERIENCE_LEVELS = List.of("Beginner", "Intermediate", "Expert");

    private static OutputDevice outputDevice;
    private static InputDevice inputDevice;

    //setter
    public static void setOutputDevice(OutputDevice outputDevice) {
        Helper.outputDevice = outputDevice;
    }

    //validate a date input (YYYY/MM/DD) with me.ancastanoev.ApplicationException

    public static String getValidatedDate(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String date = scanner.nextLine().trim();

            try {
                ApplicationException.validateDate(date);
                return date;
            } catch (ApplicationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //prompts the user for integer input with validation

    public static int getIntegerInput(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }



    //checks if the end date is after the start date

    public static boolean isEndDateAfterStartDate(String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            return end.isAfter(start);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd");
        }
    }
    //prompts the user for a valid integer input using OutputDevice

    public static int getValidIntegerInput(String prompt) throws InputException {
        while (true) {
            try {
                outputDevice.writeMessage(prompt);
                int input = Integer.parseInt(inputDevice.getLine().trim());  // Converts input to integer
                return input;
            } catch (NumberFormatException e) {
                outputDevice.writeMessage("Error: Please enter a valid integer.");
            } catch (InputException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
