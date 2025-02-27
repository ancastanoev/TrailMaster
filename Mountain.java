package me.ancastanoev;

import me.ancastanoev.io.OutputDevice;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class Mountain implements Serializable {
    private String name;
    private Climber leadClimber;
    private List<Expedition> expeditions;
    private List<Route> routes; // List to store available routes

    // Define a pattern for valid mountain names (only letters and spaces)
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");

    /**
     * Constructor with validation.
     *
     * @param name             the mountain's name
     * @param leadClimber      the lead climber
     * @param expeditionsArray an array of expeditions
     * @throws IllegalArgumentException if name contains special characters or if leadClimber is null
     */
    public Mountain(String name, Climber leadClimber, Expedition[] expeditionsArray) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Mountain name cannot be null or empty.");
        }
        if (!VALID_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Mountain name cannot contain special characters.");
        }
        if (leadClimber == null) {
            throw new IllegalArgumentException("Lead climber cannot be null.");
        }

        this.name = name;
        this.leadClimber = leadClimber;
        this.routes = new ArrayList<>(); // Properly initialize the routes list
        this.expeditions = new ArrayList<>();

        if (expeditionsArray != null) {
            for (Expedition expedition : expeditionsArray) {
                if (expedition != null) {
                    this.expeditions.add(expedition);
                }
            }
        }
    }

    /**
     * Constructor with only mountain name.
     *
     * @param mountainName the mountain's name
     * @throws IllegalArgumentException if name contains special characters
     */
    public Mountain(String mountainName) {
        if (mountainName == null || mountainName.trim().isEmpty()) {
            throw new IllegalArgumentException("Mountain name cannot be null or empty.");
        }
        if (!VALID_NAME_PATTERN.matcher(mountainName).matches()) {
            throw new IllegalArgumentException("Mountain name cannot contain special characters.");
        }

        this.name = mountainName;
        this.leadClimber = null;
        this.routes = new ArrayList<>();
        this.expeditions = new ArrayList<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    public Climber getLeadClimber() {
        return leadClimber;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public List<Expedition> getExpeditions() {
        return expeditions;
    }

    // Add Expedition
    public void addExpedition(Expedition expedition) {
        if (expedition != null) {
            expeditions.add(expedition);
        }
    }

    // Display Info
    public void displayInfo(OutputDevice outputDevice) {
        outputDevice.writeMessage("Mountain Name: " + name);
        if (leadClimber != null) {
            leadClimber.displayInfo(outputDevice);
        } else {
            outputDevice.writeMessage("No Lead Climber Assigned.");
        }
        outputDevice.writeMessage("Number of Expeditions: " + expeditions.size());

        for (Expedition expedition : expeditions) {
            expedition.displayInfo();
        }
    }

    // toText
    public String toText() {
        String leadClimberText = (leadClimber != null)
                ? leadClimber.getFirstName() + " " + leadClimber.getLastName()
                : "No Lead Climber";
        return String.format("%s,%s", name, leadClimberText);
    }

    // fromText
    public static Mountain fromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Mountain data cannot be null or empty.");
        }

        String[] parts = text.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid Mountain data format. Expected format: 'name,leadClimberName'");
        }

        String mountainName = parts[0].trim();
        String leadClimberName = parts[1].trim();

        String[] nameParts = leadClimberName.split(" ");
        if (nameParts.length < 2) {
            throw new IllegalArgumentException("Invalid lead climber name format.");
        }

        Climber leadClimber = ClimberManager.findClimberByName(nameParts[0], nameParts[1]);
        if (leadClimber == null) {
            throw new IllegalArgumentException("Lead climber not found: " + leadClimberName);
        }

        return new Mountain(mountainName, leadClimber, new Expedition[0]);
    }

    // equals and hashCode (optional but recommended)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mountain)) return false;
        Mountain mountain = (Mountain) o;
        return name.equals(mountain.name) &&
                Objects.equals(leadClimber, mountain.leadClimber) &&
                Objects.equals(expeditions, mountain.expeditions) &&
                Objects.equals(routes, mountain.routes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, leadClimber, expeditions, routes);
    }
}
