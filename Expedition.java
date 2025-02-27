package me.ancastanoev;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

public class Expedition implements Rateable, Matchable, Serializable {
    private static String startDate;
    private static String endDate;
    private static String outcome;
    private static String difficultyLevel;
    private static Route route;

    private Set<Climber> climbers;

    // Constructors


    public Expedition(String startDate, String endDate, String outcome, String difficultyLevel, Route route) {
        validateDates(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
        this.outcome = outcome;
        this.difficultyLevel = difficultyLevel;
        this.route = route;
        this.climbers = new HashSet<>();
    }

    /**
     * Constructor without outcome (defaults to empty string).
     */
    public Expedition(String startDate, String endDate, String difficultyLevel, Route route) {
        this(startDate, endDate, "", difficultyLevel, route);
    }

    /**
     * Constructor that parses Route from a string.
     */
    public Expedition(String startDate, String endDate, String status, String difficulty, String routeName) {
        validateDates(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
        this.outcome = status;
        this.difficultyLevel = difficulty;
        this.route = Route.fromText(routeName); // Ensure Route.fromText handles single names
        this.climbers = new HashSet<>();
    }

    /**
     * Validates that endDate is after startDate.
     */
    private void validateDates(String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            if (end.isBefore(start)) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd");
        }
    }

    // Getters
    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public Route getRoute() {
        return route;
    }

    public Set<Climber> getClimbers() {
        return climbers;
    }

    // Add Climber
    public void addClimber(Climber climber) {
        if (climber != null) {
            this.climbers.add(climber);
        }
    }

    // Remove Climber
    public void removeClimber(Climber climber) {
        this.climbers.remove(climber);
    }

    // Display Info
    public String displayInfo() {
        StringBuilder info = new StringBuilder();

        info.append("Expedition Start Date: ").append(startDate).append("\n");
        info.append("Expedition End Date: ").append(endDate).append("\n");
        info.append("Difficulty Level: ").append(difficultyLevel).append("\n");
        info.append("Outcome: ").append(outcome.isEmpty() ? "N/A" : outcome).append("\n");

        if (route != null) {
            info.append("Route: ").append(route.getName())
                    .append(" on Mountain: ").append(route.getMountainName()).append("\n");
        }

        info.append("Climbers:\n");
        for (Climber climber : climbers) {
            info.append(" - ").append(climber.getFirstName()).append(" ").append(climber.getLastName()).append("\n");
        }

        return info.toString();
    }

    // toText
    public static String toText() {
        String routeName = (route != null) ? route.getName() : "Unknown";
        String outcomeStr = (outcome != null) ? outcome : "Unknown";
        return String.format("%s,%s,%s,%s,%s",
                startDate != null ? startDate : "Unknown",
                endDate != null ? endDate : "Unknown",
                outcomeStr,
                difficultyLevel != null ? difficultyLevel : "Unknown",
                routeName);
    }

    // fromText
    public static Expedition fromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Input text cannot be null or empty.");
        }

        // Split by commas
        String[] parts = text.split(",");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid Expedition data format. Expected format: 'startDate,endDate,outcome,difficultyLevel,route'");
        }

        String startDate = !parts[0].trim().isEmpty() ? parts[0].trim() : null;
        String endDate = !parts[1].trim().isEmpty() ? parts[1].trim() : null;
        String outcome = !parts[2].trim().isEmpty() ? parts[2].trim() : "";
        String difficultyLevel = !parts[3].trim().isEmpty() ? parts[3].trim() : null;
        String routeName = !parts[4].trim().isEmpty() ? parts[4].trim() : null;

        Route route = (routeName != null) ? Route.fromText(routeName) : null;

        return new Expedition(startDate, endDate, outcome, difficultyLevel, route);
    }


    // matches method
    @Override
    public boolean matches(Matchable m) {
        if (m instanceof Climber climber) {
            return isAccessibleToClimber(climber);
        }
        if (m instanceof Expedition otherExpedition) {
            return this.equals(otherExpedition);
        }
        return false;
    }

    // Check if accessible to a climber based on experience level
    private boolean isAccessibleToClimber(Climber climber) {
        switch (difficultyLevel.toLowerCase()) {
            case "easy":
                return !climber.getExperienceLevel().equalsIgnoreCase("beginner");
            case "moderate":
                return !climber.getExperienceLevel().equalsIgnoreCase("beginner");
            case "difficult":
                return climber.getExperienceLevel().equalsIgnoreCase("expert");
            default:
                return false;
        }
    }

    // Rateable interface methods


    @Override
    public String getExperienceLevel() {
        return null; // Expeditions do not have an experience level
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expedition)) return false;
        Expedition that = (Expedition) o;
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(outcome, that.outcome) &&
                Objects.equals(difficultyLevel, that.difficultyLevel) &&
                Objects.equals(route, that.route);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, outcome, difficultyLevel, route);
    }

    public Route getRouteName() {
        return this.route;
    }
}
