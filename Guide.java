package me.ancastanoev;

import me.ancastanoev.io.OutputDevice;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class Guide implements Rateable, Serializable {
    private String name;
    private String experienceLevel;

    // Define allowed experience levels
    private static final Set<String> ALLOWED_EXPERIENCE_LEVELS = Set.of("Beginner", "Intermediate", "Expert");


    public Guide(String name, String experienceLevel) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Guide name cannot be null or empty");
        }
        if (!ALLOWED_EXPERIENCE_LEVELS.contains(experienceLevel)) {
            throw new IllegalArgumentException("Invalid experience level");
        }
        this.name = name;
        this.experienceLevel = experienceLevel;
    }

    public String getName() {
        return name;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void displayInfo(OutputDevice outputDevice) {
        outputDevice.writeMessage("Guide Name: " + name);
        outputDevice.writeMessage("Experience Level: " + experienceLevel);
    }

    /**
     * Converts the Guide object to a text representation.
     *
     * @return a comma-separated string of name and experience level
     */
    public String toText() {
        // Ensure fields are non-null, with placeholders if they are null
        String guideName = (name != null) ? name : "unknown";
        String guideExperience = (experienceLevel != null) ? experienceLevel : "unknown";

        return String.format("%s,%s", guideName, guideExperience);
    }

    /**
     * Parses a Guide object from a text representation.
     *
     * @param line the comma-separated string representing a guide
     * @return a Guide object
     * @throws IllegalArgumentException if the format is invalid or experience level is invalid
     */
    public static Guide fromText(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Guide data cannot be null or empty");
        }

        String[] parts = line.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid guide data format. Expected format: 'name,experienceLevel'");
        }

        String name = parts[0].trim();
        String experience = parts[1].trim();

        return new Guide(name, experience);
    }

    @Override
    public String getDifficultyLevel() {
        return null;
    }

    // equals and hashCode methods (optional but recommended)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Guide)) return false;
        Guide guide = (Guide) o;
        return name.equals(guide.name) &&
                experienceLevel.equals(guide.experienceLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, experienceLevel);
    }
}
