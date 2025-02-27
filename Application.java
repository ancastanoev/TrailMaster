package me.ancastanoev;

import me.ancastanoev.io.InputDevice;
import me.ancastanoev.io.InputException;
import me.ancastanoev.io.OutputDevice;

import java.io.IOException;
import java.util.*;
import java.io.FileNotFoundException;

;

public class Application {
    private static final List<String> EXPERIENCE_LEVELS = List.of("Beginner", "Intermediate", "Expert");
    private static InputDevice inputDevice = new InputDevice();
    private static OutputDevice outputDevice = new OutputDevice();

    //data collections
    private static List<Climber> climbers = new ArrayList<>();
    private static ClimberManager climberManager = new ClimberManager(climbers);
    private static String currentRole;
    public static List<Climber> getClimbers() {
        return climbers;
    }

    public static List<Mountain> mountains = new ArrayList<>();
    private static List<Route> paths = new ArrayList<>();
    private static List<Guide> guides = new ArrayList<>();
    private static List<Expedition> expeditions = new ArrayList<>();

    // flag used to check if data was loaded
    private static boolean dataLoaded = false;

    // entry point for running the application as a regular user
    public static void runAsUser() throws ApplicationException, InputException, IOException, ClassNotFoundException {
        System.out.println("You are logged in as a regular user. You have limited access.");

        // we attempt to load existing climbers from file
        try {
            loadData(false);
        } catch (FileNotFoundException e) {
            System.out.println("No existing data found. Starting fresh session.");
            climbers = new ArrayList<>();
            // we start with an empty list if file is missing
        }

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            //user menu options
            System.out.println("Welcome to the Climber system.");
            System.out.println("Please choose an option:");
            System.out.println("1. Log in");
            System.out.println("2. Create a new account");
            System.out.println("3. Exit");

            int userChoice = 0;

            // validate input: only accept 1, 2, or 3
            while (userChoice != 1 && userChoice != 2 && userChoice != 3) {
                System.out.print("Enter 1 to log in, 2 to create a new account, or 3 to exit: ");
                try {
                    userChoice = Integer.parseInt(scanner.nextLine().trim());
                    if (userChoice != 1 && userChoice != 2 && userChoice != 3) {
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number (1, 2, or 3).");
                }
            }

            Climber climber = null;

            if (userChoice == 1) {
                // log in
                System.out.println("Please log in.");
                String firstName = getInput(scanner, "Enter your first name (only letters, hyphens, or apostrophes): ", "[a-zA-Z'-]{1,50}");
                String lastName = getInput(scanner, "Enter your last name (only letters, hyphens, or apostrophes): ", "[a-zA-Z'-]{1,50}");

                climber = ClimberManager.findClimberByName(firstName, lastName);
                if (climber != null) {
                    System.out.println("Welcome back, " + climber.getFirstName() + " " + climber.getLastName());
                } else {
                    System.out.println("Climber not found. Please try again or create a new account.");
                    continue;
                }
            } else if (userChoice == 2) {
                // create a new account
                System.out.println("Let's create a new account!");

                String firstName = getInput(scanner, "Enter your first name (only letters, hyphens, or apostrophes): ", "[a-zA-Z'-]{1,50}");
                String lastName = getInput(scanner, "Enter your last name (only letters, hyphens, or apostrophes): ", "[a-zA-Z'-]{1,50}");

                // check if climber already exists
                if (ClimberManager.findClimberByName(firstName, lastName) != null) {
                    System.out.println("An account with that name already exists. Please log in.");
                    continue;
                }

                // we collect experience level and contact info for new climber
                String experienceLevel = getExperienceLevel(scanner);
                String contactInfo = getContactInfo(scanner);

                climber = new Climber(firstName, lastName, experienceLevel, contactInfo);
                climbers.add(climber);

                System.out.println("Account created successfully!");
                System.out.println("Welcome, " + climber.getFirstName() + " " + climber.getLastName());

                saveData();
                //we save the new climber to the climbers file
            } else if (userChoice == 3) {
                System.out.println("Exiting application...");
                return;
            }

            // second menu options for users after logging in
            while (isRunning) {
                System.out.println("\nEnter a command:");
                System.out.println("Options: 1- Plan me.ancastanoev.Expedition, 2- Exit");

                String command = scanner.nextLine().trim();
                switch (command) {
                    case "1":
                        if (climber != null) {
                            planExpedition(climber);
                        } else {
                            System.out.println("You need to log in first to plan an expedition.");
                        }
                        break;
                    case "2":
                        System.out.println("Exiting application...");
                        saveData();
                        // we save data before exiting
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid command. Please enter 1 for Plan me.ancastanoev.Expedition or 2 for Exit.");
                }
            }
        }

        scanner.close();
    }

    //entry point for running the application as an admin
    public static void runAsAdmin() throws InputException, IOException, ClassNotFoundException {
        System.out.println("You are logged in as an admin. You have full access.");

        // option to load (or not load) teh data as an admin
        loadData(true);
        System.out.println("Climbers loaded: " + climbers.size() + " climbers found.");

        Scanner scanner = new Scanner(System.in);

        //admin menu options
        while (true) {
            displayAdminMenu();
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "1":
                    ClimberManager.createClimberProfile();
                    break;
                case "2":
                    addMountain();
                    break;
                case "3":
                    RouteManager.addPath("Trail B", "Difficult", "Mountain B");
                    break;
                case "4":
                    addGuide();
                    break;
                case "5":
                    ClimberManager.sortClimbersByExperienceAndName();
                    break;
                case "6":
                    RouteManager.sortRoutesByDifficultyAndName();
                    break;
                case "7":
                    ClimberManager.displayClimbersGroupedByExperience();
                    break;
                case "8":
                    ClimberManager.changeClimberExperienceLevel("John", "Doe", "Intermediate");
                    break;
                case "exit":
                    System.out.println("Exiting application...");
                    saveData();
                    // we save the data before exiting
                    return;
                default:
                    System.out.println("Unknown command. Please try again.");
            }
        }
    }

    //display the admin menu optins

    private static void displayAdminMenu() {
        System.out.println("\n--- Admin Menu ---");
        System.out.println("1. Create Climber Profile");
        System.out.println("2. Add me.ancastanoev.Mountain");
        System.out.println("3. Add Path");
        System.out.println("4. Add Guide");
        System.out.println("5. Sort Climbers by Experience and Name");
        System.out.println("6. Sort Routes by Difficulty and Name");
        System.out.println("7. Group Climbers by Experience");
        System.out.println("8. Change Climber Experience Level");
        System.out.println("Type 'exit' to exit the application.");
        System.out.print("Select an option: ");
    }

    //function to load the data from files into the application's data structures
    public static void loadData(boolean isAdmin) throws InputException, IOException, ClassNotFoundException {
        if (isAdmin) {
            outputDevice.writeMessage("Do you want to load the previous session? (y/n): ");
            String userInput = inputDevice.getLine().trim().toLowerCase();

            // validate user input for loading data
            while (!(userInput.equals("y") || userInput.equals("n"))) {
                outputDevice.writeMessage("Invalid input. Please enter 'y' for yes or 'n' for no.");
                userInput = inputDevice.getLine().trim().toLowerCase();
            }

            if (userInput.equals("y")) {
                //loadSessionData();
            } else {
                outputDevice.writeMessage("Starting a new session...");
            }
        } else {
            // automatically load data for regular users taht are not admin without prompting
            //loadSessionData();
        }
    }


    // function to save the current data to files
    public static void saveData() {
        // we only save data if it was loaded or if the list has data
        if (dataLoaded || !climbers.isEmpty()) {

            FileHandler.saveClimbers(climbers);
        }
        if (dataLoaded || !mountains.isEmpty()) {
            FileHandler.saveMountains(mountains);
        }
        if (dataLoaded || !expeditions.isEmpty()) {
            FileHandler.saveExpeditions(expeditions);
        }
        if (dataLoaded || !guides.isEmpty()) {
            FileHandler.saveGuides(guides);
        }
        if (dataLoaded || !paths.isEmpty()) {
            FileHandler.saveRoutes(paths);
        }
    }

    // function for the admin; it adds a new mountain to the system
    public static void addMountain() throws InputException {
        if (climbers.isEmpty()) {
            outputDevice.writeMessage("No climbers available to set as leadClimber. Please add climbers first.");
            return;
            // stop if no climbers are available
            //because every montain should have a lead climber ( the first one)
            //in the sense that the app should not plan expeditions for "untested" mountains
        }

        String mountainName = getMountainName();

        // validations
        if (!mountainName.matches("[A-Za-z][A-Za-z0-9]*")) {
            outputDevice.writeMessage("Invalid mountain name. me.ancastanoev.Mountain name should only contain letters or letters and digits, but not only digits.");
            return;
        }

        Climber leadClimber = climbers.get(0); // Ensure at least one climber is available for selection

        //we create a new me.ancastanoev.Mountain object
        Mountain mountain = new Mountain(mountainName, leadClimber, new Expedition[0]);
        mountains.add(mountain);

        outputDevice.writeMessage("me.ancastanoev.Mountain added successfully!");
    }

    //function for user:
    public static void planExpedition(Climber loggedInClimber) throws ApplicationException, IOException, ClassNotFoundException {
        String userName = loggedInClimber.getFirstName() + " " + loggedInClimber.getLastName();
        String experienceLevel = loggedInClimber.getExperienceLevel();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Planning expedition for: " + userName);
        System.out.println("Experience Level: " + experienceLevel);

        // step 1: select a mountain
        Mountain selectedMountain = null;
        while (selectedMountain == null) {
            if (mountains.isEmpty()) {
                System.out.println("No mountains available. Please add a mountain first.");
                return;
            }
            selectedMountain = getMountainSelection(scanner);
        }

        // step 2: load and filter routes based on experience level, then select a route
        Route selectedRoute = null;
        while (selectedRoute == null) {
            List<Route> allRoutes = RouteManager.loadRoutesForMountain(selectedMountain);

            if (allRoutes.isEmpty()) {
                System.out.println("No routes exist for this mountain.");
                return;
            }

            List<Route> accessibleRoutes = allRoutes.stream()
                    .filter(route -> isRouteAccessibleForExperience(route, experienceLevel))
                    .toList();

            if (accessibleRoutes.isEmpty()) {
                System.out.println("No accessible routes available for your experience level on this mountain.");
                return;
            }

            selectedRoute = RouteManager.getRouteSelection(accessibleRoutes, scanner);
        }

        // step 3: get start and end dates for the expedition
        String startDate = Helper.getValidatedDate("Enter the start date of the expedition (YYYY/MM/DD): ", scanner);
        String endDate = null;

        while (endDate == null) {
            endDate = Helper.getValidatedDate("Enter the end date of the expedition (YYYY/MM/DD): ", scanner);
            if (!Helper.isEndDateAfterStartDate(startDate, endDate)) {
                System.out.println("End date cannot be before or the same as the start date. Please enter a valid end date.");
                endDate = null;
            }
        }

        // step 4: filter climbers by experience level and exclude the logged-in climber
        List<Climber> eligibleClimbers = climbers.stream()
                .filter(climber -> !climber.equals(loggedInClimber) && climber.getExperienceLevel().equalsIgnoreCase(experienceLevel))
                .toList();
        int availableClimbers = eligibleClimbers.size();

        if (availableClimbers > 0) {
            System.out.printf("There are %d other climbers with matching experience level available to join this expedition.\n", availableClimbers);
        } else {
            System.out.println("No other climbers with matching experience level are available to join this expedition.");
        }

        int numClimbers = -1;
        while (numClimbers < 0 || numClimbers > availableClimbers) {
            numClimbers = Helper.getIntegerInput("How many other climbers would you like to join the expedition? (0 to " + availableClimbers + "): ", scanner);
            if (numClimbers < 0 || numClimbers > availableClimbers) {
                System.out.println("Invalid number. Please enter a value between 0 and " + availableClimbers + ".");
            }
        }

        // step 5: select eligible climbers based on the chosen number
        Set<Climber> selectedClimbers = new HashSet<>();
        if (numClimbers > 0) {
            selectedClimbers = new HashSet<>(eligibleClimbers.subList(0, numClimbers)); // Select the first 'numClimbers' from eligible climbers
        }

        // step 6: find a guide for the expedition
        Guide guide = findGuide(selectedMountain, experienceLevel);
        if (guide == null) {
            System.out.println("No guide available with the required experience level. Cannot proceed with expedition planning.");
            return;
        }

        // step 7: create and add expedition
        Expedition expedition = new Expedition(startDate, endDate, userName, selectedRoute.getDifficultyLevel(), selectedRoute);
        expedition.addClimber(loggedInClimber);  // Add logged-in user as climber
        selectedClimbers.forEach(expedition::addClimber);  // Add matched climbers

        // step 8: display expedition info and save
        //expedition.displayInfo(new OutputDevice());
        expeditions.add(expedition);
        System.out.println("me.ancastanoev.Expedition planned successfully with guide: " + guide.getName());

        // add expedition to logged-in climber's history
        loggedInClimber.getExpeditionHistory().add(expedition);
    }


    // function; adds a new guide to the system.
    public static void addGuide() throws InputException {
        while (true) {
            try {
                // step 1: Get the guide's name
                outputDevice.writeMessage("Enter guide's name: ");
                String guideName = inputDevice.getLine().trim();

                if (guideName.isEmpty()) {
                    throw new ApplicationException("Guide name cannot be empty.");
                }

                // step 2: get the guide's experience level
                String experienceLevel = null;
                while (experienceLevel == null) {
                    outputDevice.writeMessage("Enter guide's experience level (Beginner, Intermediate, Expert): ");
                    experienceLevel = inputDevice.getLine().trim();

                    if (!EXPERIENCE_LEVELS.stream().anyMatch(experienceLevel::equalsIgnoreCase)) {
                        outputDevice.writeMessage("Invalid experience level. Please enter one of: Beginner, Intermediate, Expert.");
                        experienceLevel = null; // Reset the experienceLevel and ask again
                    }
                }

                // step 3: create and add the guide if all steps are successful
                Guide guide = new Guide(guideName, experienceLevel);
                guides.add(guide);
                outputDevice.writeMessage("Guide added successfully!");
                break;

            } catch (ApplicationException e) {
                outputDevice.writeMessage("Error: " + e.getMessage());

                // retry prompt for validation of "y" or "n"
                String response;
                while (true) {
                    outputDevice.writeMessage("Would you like to try again? (y/n): ");
                    response = inputDevice.getLine().trim().toLowerCase();

                    if (response.equals("y")) {
                        break;
                    } else if (response.equals("n")) {
                        outputDevice.writeMessage("Exiting guide addition.");
                        return;
                    } else {
                        outputDevice.writeMessage("Invalid response. Please enter 'y' for yes or 'n' for no.");
                    }
                }
            }
        }
    }

    //get validated user input based on a reggex

    private static String getInput(Scanner scanner, String prompt, String regex) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.matches(regex)) {
                System.out.println("Invalid input. Please try again.");
            }
        } while (!input.matches(regex));
        return input;
    }

    // we validate the experience level from the user

    private static String getExperienceLevel(Scanner scanner) {
        String experienceLevel;
        do {
            System.out.print("Enter your experience level (Beginner, Intermediate, Expert): ");
            experienceLevel = scanner.nextLine().trim();
        } while (!(experienceLevel.equalsIgnoreCase("Beginner") ||
                experienceLevel.equalsIgnoreCase("Intermediate") ||
                experienceLevel.equalsIgnoreCase("Expert")));
        return experienceLevel;
    }

    // validate email

    private static String getContactInfo(Scanner scanner) {
        String contactInfo;
        String emailPattern = "^[a-z0-9_+&*-]+(?:\\.[a-z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        do {
            System.out.print("Enter your contact information (email): ");
            contactInfo = scanner.nextLine().trim();
            if (!contactInfo.matches(emailPattern)) {
                System.out.println("Invalid contact information. Please enter a valid email address.");
            }
        } while (!contactInfo.matches(emailPattern));
        return contactInfo;
    }

    // function to retrieve a valid mountain selection from the user based on index

    private static Mountain getMountainSelection(Scanner scanner) {
        Mountain selectedMountain = null;
        while (selectedMountain == null) {
            System.out.println("Select a mountain for the expedition:");
            for (int i = 0; i < mountains.size(); i++) {
                System.out.println(i + ": " + mountains.get(i).getName());
            }

            int mountainIndex = Helper.getIntegerInput("Enter the index of the selected mountain: ", scanner);
            if (mountainIndex >= 0 && mountainIndex < mountains.size()) {
                selectedMountain = mountains.get(mountainIndex);
            } else {
                System.out.println("Invalid mountain index. Please try again.");
            }
        }
        return selectedMountain;
    }

    //function to retrieve and validate the mountain based on nname

    private static String getMountainName() throws InputException {
        while (true) {
            outputDevice.writeMessage("Enter the mountain's name: ");
            String mountainName = inputDevice.getLine().trim();

            // validate
            if (mountainName.isEmpty() || mountainName.length() < 3 || mountainName.length() > 50 || !mountainName.matches("^[a-zA-Z0-9\\s]+$")) {
                outputDevice.writeMessage("me.ancastanoev.Mountain name cannot be empty or contain special characters. It should be between 3 and 50 characters.");
            } else {
                // we must have unique mountain names
                boolean isUnique = true;
                for (Mountain existingMountain : mountains) {
                    if (existingMountain.getName().equalsIgnoreCase(mountainName)) {
                        isUnique = false;
                        break;
                    }
                }

                if (!isUnique) {
                    outputDevice.writeMessage("me.ancastanoev.Mountain with this name already exists. Please choose a different name.");
                } else {
                    return mountainName;
                }
            }
        }
    }

    // function to determine if a route is accessible based on the climber experience

    public static boolean isRouteAccessibleForExperience(Route route, String experienceLevel) {
        switch (experienceLevel.toLowerCase()) {
            case "beginner":
                return "easy".equalsIgnoreCase(route.getDifficultyLevel());
            case "intermediate":
                return "easy".equalsIgnoreCase(route.getDifficultyLevel()) || "moderate".equalsIgnoreCase(route.getDifficultyLevel());
            case "expert":
                return true;  // Experts can access all difficulty levels
            default:
                return false;
        }
    }

    // find guide for expedition based on experience level

    private static Guide findGuide(Mountain mountain, String experienceLevel) {
        for (Guide guide : guides) {
            if (guide.getExperienceLevel().equalsIgnoreCase(experienceLevel)) {
                return guide;
            }
        }
        return null;
    }

    //getters
    public static List<Route> getPaths() {
        return paths;
    }

    public static OutputDevice getOutputDevice() {
        return outputDevice;
    }

    public static Collection<Guide> getGuides() {

        return guides;
    }

    public static boolean isValidExperienceLevel(String climberExperience) {
        List<String> validLevels = List.of("Beginner", "Intermediate", "Expert");
        return validLevels.contains(climberExperience);
    }

    public static <E> List<E> getMountains() {
        return (List<E>) mountains;
    }

    private static Climber loggedInUser; // Tracks the currently logged-in user

    public static Climber getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(Climber user) {
        loggedInUser = user;
    }
    public static String getRole() {
        return currentRole;
    }

    // Method to set the current role
    public static void setRole(String role) {
        currentRole = role;
    }
}
