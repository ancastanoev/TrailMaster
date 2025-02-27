package me.ancastanoev;

import me.ancastanoev.io.InputDevice;
import me.ancastanoev.io.InputException;
import me.ancastanoev.io.OutputDevice;

import java.util.*;
import java.util.stream.Collectors;

public class ClimberManager {

    private static List<Climber> climbers;
    private static List<Mountain> mountains =Application.mountains;
    private static final List<String> EXPERIENCE_LEVELS = List.of("Beginner", "Intermediate", "Expert");

    private static OutputDevice outputDevice;
    private static InputDevice inputDevice;

    // variables to control sorting preferences
    private static boolean ascendingExperience = true;
    private static boolean ascendingName = true;

    //constructors
    public ClimberManager(List<Climber> climbers) {
        this.climbers = climbers != null ? climbers : new ArrayList<>();
    }


    public static void setOutputDevice(OutputDevice outputDevice) {
        ClimberManager.outputDevice = outputDevice;
    }

    //function to find a climber by first and last name
    public static Climber findClimberByName(String firstName, String lastName) {
        if (climbers == null) {
            System.out.println("There are no climbers.");
        }
        for (Climber climber : climbers) {
            if (climber.getFirstName().equals(firstName) && climber.getLastName().equals(lastName)) {
                return climber;
            }
        }
        return null;
    }


    public static void setClimbers(List<Climber> climbers) {
        ClimberManager.climbers = climbers;
    }

    //generates a progress report for a climber.

    public static void generateClimberProgressReport() throws InputException {

        outputDevice.writeMessage("Enter climber's first name for progress report:");
        String firstName = inputDevice.getLine().trim();

        outputDevice.writeMessage("Enter climber's last name for progress report:");
        String lastName = inputDevice.getLine().trim();

        // find teh climber
        Climber climber = ClimberManager.findClimberByName(firstName, lastName);
        if (climber != null) {

            outputDevice.writeMessage("Climber Progress Report");
            outputDevice.writeMessage("========================");
            outputDevice.writeMessage("Name: " + climber.getFirstName() + " " + climber.getLastName());
            outputDevice.writeMessage("Experience Level: " + climber.getExperienceLevel());

            List<Expedition> expeditionHistory = climber.getExpeditionHistory();
            outputDevice.writeMessage("Total Completed Expeditions: " + expeditionHistory.size());


            if (!expeditionHistory.isEmpty()) {
                outputDevice.writeMessage("me.ancastanoev.Expedition Details:");
                for (Expedition expedition : expeditionHistory) {
                    String routeName = expedition.getRoute() != null ? expedition.getRoute().getName() : "Unknown me.ancastanoev.Route";
                    outputDevice.writeMessage("  - me.ancastanoev.Expedition on me.ancastanoev.Route: " + routeName);
                    outputDevice.writeMessage("    Climbers: " + expedition.getClimbers());
                }
            } else {
                outputDevice.writeMessage("No expeditions completed yet.");
            }
        } else {
            outputDevice.writeMessage("Climber not found.");
        }
    }

    //display climbers grouped by their experience level

    public static void displayClimbersGroupedByExperience() {
        Map<String, List<Climber>> climbersByExperience = groupClimbersByExperience();
        outputDevice.writeMessage("Climbers grouped by experience level:");
        for (Map.Entry<String, List<Climber>> entry : climbersByExperience.entrySet()) {
            outputDevice.writeMessage("Experience Level: " + entry.getKey());
            for (Climber climber : entry.getValue()) {
                outputDevice.writeMessage(climber.getFirstName() + " " + climber.getLastName());
            }
        }
    }

    // group climbers by their experience level.

    public static Map<String, List<Climber>> groupClimbersByExperience() {
        return climbers.stream()
                .collect(Collectors.groupingBy(climber -> climber.getExperienceLevel().toLowerCase()));
    }

    //sort climbers by experience level and name

    public static void sortClimbersByExperienceAndName() {
        climbers.sort(Comparator
                .comparing((Climber c) -> c.getExperienceLevel().toLowerCase(),
                        ascendingExperience ? Comparator.naturalOrder() : Comparator.reverseOrder())
                .thenComparing((Climber c) -> c.getLastName().toLowerCase(),
                        ascendingName ? Comparator.naturalOrder() : Comparator.reverseOrder()));

        outputDevice.writeMessage("Climbers sorted by experience and name:");
        for (Climber climber : climbers) {
            outputDevice.writeMessage(climber.getFirstName() + " " + climber.getLastName() + " - Experience: " + climber.getExperienceLevel());
        }
    }

    // function for admin, creates a new climber profile

    public static void createClimberProfile() throws InputException {
        try {
            String firstName = getFirstName();
            String lastName = getLastName();
            String experience = getExperienceLevel();
            String contactInfo = ClimberManager.getContactInfo();

            // we check for existing climber with the same first and last name
            if (doesClimberExist(firstName, lastName)) {
                throw ApplicationException.climberAlreadyExists(firstName, lastName);
            }

            // create the climber profile if no duplicates are found
            Climber climber = new Climber(firstName, lastName, experience, contactInfo);
            climbers.add(climber);
            outputDevice.writeMessage("Climber profile created successfully!");
        } catch (ApplicationException e) {
            outputDevice.writeMessage("Error: " + e.getMessage());
        }
    }

    //function to automate experience leel updates for all climbers based on their expedition count

    public static void automateExperienceLevelUpdates() {
        for (Climber climber : climbers) {
            int completedExpeditions = climber.getExpeditionHistory().size();
            String newExperienceLevel = determineExperienceLevel(completedExpeditions);

            if (!climber.getExperienceLevel().equals(newExperienceLevel)) {
                climber.setExperienceLevel(newExperienceLevel);
                outputDevice.writeMessage("Climber " + climber.getFirstName() + " " + climber.getLastName()
                        + " automatically promoted to " + newExperienceLevel);
            }
        }
    }

    // function for admin, changes a climber's experience level manually

    public static void changeClimberExperienceLevel(String john, String doe, String intermediate) throws InputException {
        outputDevice.writeMessage("Enter the first name of the climber to change experience level:");
        String firstName = inputDevice.getLine().trim();

        // validate
        while (firstName.isEmpty() || !firstName.matches("[a-zA-Z'-]+") || firstName.length() < 1 || firstName.length() > 50) {
            outputDevice.writeMessage("Invalid input. First name must contain only letters, hyphens, or apostrophes (1-50 characters).");
            firstName = inputDevice.getLine().trim();
        }

        outputDevice.writeMessage("Enter the last name of the climber to change experience level:");
        String lastName = inputDevice.getLine().trim();

        // validate
        while (lastName.isEmpty() || !lastName.matches("[a-zA-Z'-]+") || lastName.length() < 1 || lastName.length() > 50) {
            outputDevice.writeMessage("Invalid input. Last name must contain only letters, hyphens, or apostrophes (1-50 characters).");
            lastName = inputDevice.getLine().trim();
        }

        // find the climber by name
        Climber climber = ClimberManager.findClimberByName(firstName, lastName);
        if (climber != null) {
            outputDevice.writeMessage("Current Experience Level: " + climber.getExperienceLevel());

            outputDevice.writeMessage("Enter new experience level (Beginner, Intermediate, Expert):");
            String newExperienceLevel = inputDevice.getLine().trim();

            // validate
            while (!(newExperienceLevel.equalsIgnoreCase("Beginner") ||
                    newExperienceLevel.equalsIgnoreCase("Intermediate") ||
                    newExperienceLevel.equalsIgnoreCase("Expert"))) {
                outputDevice.writeMessage("Invalid experience level entered. Please enter one of the following: Beginner, Intermediate, Expert.");
                newExperienceLevel = inputDevice.getLine().trim();
            }

            climber.setExperienceLevel(newExperienceLevel);
            outputDevice.writeMessage("Experience level updated successfully!");
        } else {
            outputDevice.writeMessage("Climber not found.");
        }
    }

    //finds  a nr of X (specified) matching climbers based on experience level
    //for plann expedition

    static Set<Climber> findMatchingClimbers(String experienceLevel, int numClimbers) throws ApplicationException {
        Set<Climber> matchingClimbers = new HashSet<>();

        // validate
        if (!EXPERIENCE_LEVELS.stream().anyMatch(exp -> exp.equalsIgnoreCase(experienceLevel))) {
            throw new ApplicationException("Invalid experience level. Must be one of: Beginner, Intermediate, Expert.");
        }

        // we filter the climbers
        for (Climber climber : climbers) {
            if (climber.getExperienceLevel().equalsIgnoreCase(experienceLevel) && matchingClimbers.size() < numClimbers) {
                matchingClimbers.add(climber);
            }
        }

        return matchingClimbers;
    }


    //  get the climber first name with validation

    private static String getFirstName() throws InputException {
        while (true) {
            outputDevice.writeMessage("Enter climber's first name: ");
            String firstName = inputDevice.getLine().trim();

            // validate first name (only letters, spaces, hyphens, and apostrophes)
            if (firstName.isEmpty() || !firstName.matches("^[a-zA-Z\\s'-]+$")) {
                outputDevice.writeMessage("First name should only contain letters, spaces, hyphens, and apostrophes.");
            } else {
                return firstName;
            }
        }
    }

    //same as above but for last name
    private static String getLastName() throws InputException {
        while (true) {
            outputDevice.writeMessage("Enter climber's last name: ");
            String lastName = inputDevice.getLine().trim();

            if (lastName.isEmpty() || !lastName.matches("^[a-zA-Z\\s'-]+$")) {
                outputDevice.writeMessage("Last name should only contain letters, spaces, hyphens, and apostrophes.");
            } else {
                return lastName;
            }
        }
    }

    // get the climber experience level with validation

    private static String getExperienceLevel() throws InputException {
        while (true) {
            outputDevice.writeMessage("Enter climber's experience level (Beginner, Intermediate, Expert): ");
            String experience = inputDevice.getLine().trim();

            if (!(experience.equalsIgnoreCase("Beginner") || experience.equalsIgnoreCase("Intermediate") || experience.equalsIgnoreCase("Expert"))) {
                outputDevice.writeMessage("Experience level must be 'Beginner', 'Intermediate', or 'Expert'.");
            } else {
                return experience;
            }
        }
    }

    // get email wiyh validatio

    static String getContactInfo() throws InputException {
        while (true) {
            outputDevice.writeMessage("Enter climber's contact info: ");
            String contactInfo = inputDevice.getLine().trim();

            // not empty
            if (contactInfo.isEmpty()) {
                outputDevice.writeMessage("Contact info cannot be empty or just spaces.");
            } else {
                // email format
                String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                        "[a-zA-Z0-9_+&*-]+)*@" +
                        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                if (!contactInfo.matches(emailPattern)) {
                    outputDevice.writeMessage("Invalid email format.");
                } else {
                    return contactInfo;
                }
            }
        }
    }

    //check if a climber already exists

    public static boolean doesClimberExist(String firstName, String lastName) {
        return climbers.stream().anyMatch(c ->
                c.getFirstName().equalsIgnoreCase(firstName) &&
                        c.getLastName().equalsIgnoreCase(lastName));
    }

    //determine the experience level based on nr ofcompleted expeditions

    private static String determineExperienceLevel(int completedExpeditions) {
        if (completedExpeditions <= 2) {
            return "Beginner";
        } else if (completedExpeditions <= 5) {
            return "Intermediate";
        } else {
            return "Expert";
        }
    }


    public static void addClimber(Climber climber) {
        FileHandler writer = new FileHandler();
        String climberData = climber.getFirstName() + " " + climber.getLastName() + "," + climber.getExperienceLevel();
        writer.append(climberData + System.lineSeparator(), "climbers.txt");
        climbers.add(climber); // Add climber to the in-memory list
        outputDevice.writeMessage("Climber saved successfully!");
    }


    public static List<Climber> getClimbers() { return  climbers;
    }
}



