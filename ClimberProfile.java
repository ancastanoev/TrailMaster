package me.ancastanoev.profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a climber’s profile.
 */
public class ClimberProfile {
    private String firstName;
    private String lastName;
    private String experienceLevel;
    private String contactInfo;
    private String profilePictureUrl;
    private String bio;
    private List<String> completedExpeditions;

    public ClimberProfile() {
        this.completedExpeditions = new ArrayList<>();
    }

    public ClimberProfile(String firstName, String lastName, String experienceLevel, String contactInfo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.experienceLevel = experienceLevel;
        this.contactInfo = contactInfo;
        this.profilePictureUrl = "";
        this.bio = "";
        this.completedExpeditions = new ArrayList<>();
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public List<String> getCompletedExpeditions() { return completedExpeditions; }
    public void setCompletedExpeditions(List<String> completedExpeditions) { this.completedExpeditions = completedExpeditions; }


    public void addCompletedExpedition(String expeditionName) {
        this.completedExpeditions.add(expeditionName);
    }
}
