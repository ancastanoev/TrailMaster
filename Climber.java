package me.ancastanoev;

import me.ancastanoev.io.OutputDevice;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Climber implements Rateable, Matchable, Comparable<Climber>, Serializable {

    private String firstName;
    private String lastName;
    private String experienceLevel;
    private String contactInfo;
    private List<Expedition> expeditionHistory;

    // Constructors
    public Climber(String firstName, String lastName, String experienceLevel, String contactInfo) {
        validateEmail(contactInfo);
        validateExperienceLevel(experienceLevel);

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.experienceLevel = experienceLevel.trim();
        this.contactInfo = contactInfo.trim();
        this.expeditionHistory = new ArrayList<>();
    }

    public Climber(String firstName, String lastName, String experienceLevel) {
        this(firstName, lastName, experienceLevel, "");
    }

    public Climber(String userName, String experienceLevel) {
        this(userName, "", experienceLevel, "");
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    @Override
    public String getDifficultyLevel() {
        return "";
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public List<Expedition> getExpeditionHistory() {
        return expeditionHistory;
    }

    // Setters
    public void setExperienceLevel(String newExperienceLevel) {
        validateExperienceLevel(newExperienceLevel);
        this.experienceLevel = newExperienceLevel.trim();
    }

    // Functionality
    public void addExpedition(Expedition expedition) {
        expeditionHistory.add(expedition);
        updateExperienceLevel(); // Update experience level based on expedition count
    }

    private void updateExperienceLevel() {
        int completedExpeditions = expeditionHistory.size();

        if (completedExpeditions <= 2) {
            this.experienceLevel = "Beginner";
        } else if (completedExpeditions <= 5) {
            this.experienceLevel = "Intermediate";
        } else {
            this.experienceLevel = "Expert";
        }
    }

    public void displayInfo(OutputDevice outputDevice) {
        System.out.println("Climber: " + firstName + " " + lastName);
        System.out.println("Experience Level: " + experienceLevel);
        System.out.println("Completed Expeditions: " + expeditionHistory.size());
    }

    // Matchable Logic
    @Override
    public boolean matches(Matchable m) {
        if (m instanceof Climber) {
            return this.equals(m);
        } else if (m instanceof Expedition) {
            Expedition expedition = (Expedition) m;
            return switch (expedition.getDifficultyLevel().toLowerCase()) {
                case "easy" -> !experienceLevel.equalsIgnoreCase("beginner");
                case "moderate" -> !experienceLevel.equalsIgnoreCase("beginner");
                case "difficult" -> experienceLevel.equalsIgnoreCase("expert");
                default -> false;
            };
        }
        return false;
    }

    // Comparable Logic
    @Override
    public int compareTo(Climber other) {
        return this.experienceLevel.compareToIgnoreCase(other.experienceLevel);
    }

    // Utilities
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Climber climber = (Climber) o;
        return Objects.equals(firstName, climber.firstName) &&
                Objects.equals(lastName, climber.lastName) &&
                Objects.equals(experienceLevel, climber.experienceLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, experienceLevel);
    }

    public String toText() {
        return String.format("%s,%s,%s,%s", firstName, lastName, experienceLevel, contactInfo);
    }

    public static Climber fromText(String text) {
        String[] parts = text.split(",");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid Climber data format. Expected format: 'firstName,lastName,experienceLevel,contactInfo'");
        }

        String firstName = parts[0].trim();
        String lastName = parts[1].trim();
        String experienceLevel = parts[2].trim();
        String contactInfo = parts[3].trim();

        if (firstName.isEmpty() || lastName.isEmpty() || experienceLevel.isEmpty()) {
            throw new IllegalArgumentException("One or more fields are empty in Climber data.");
        }

        return new Climber(firstName, lastName, experienceLevel, contactInfo);
    }

    @Override
    public String toString() {
        return "Climber{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", experienceLevel='" + experienceLevel + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                '}';
    }

    public String getExpeditions() {
        return expeditionHistory.toString();
    }

    // Validation Methods
    private void validateEmail(String email) {
        if (!email.isEmpty() && !email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }

    private void validateExperienceLevel(String experienceLevel) {
        List<String> validLevels = List.of("Beginner", "Intermediate", "Expert");
        if (!validLevels.contains(experienceLevel)) {
            throw new IllegalArgumentException("Invalid experience level. Valid levels are: Beginner, Intermediate, Expert.");
        }
    }

    public String getEmail() { return contactInfo; }
    }

