package me.ancastanoev;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    private TextField experienceField;

    @FXML
    private Button displayClimbersButton;

    @FXML
    private TextArea climbersDisplayArea;

    @FXML
    protected void onHelloButtonClick() {
       // welcomeText.setText("Welcome to JavaFX me.ancastanoev.Application.Application!");
    }

    @FXML
    protected void initialize() throws IOException {
        // Load climbers from climbers.txt file directly
       Application.getClimbers().clear();
        Application.getClimbers().addAll(FileHandler.loadClimbers("climbers.txt"));


    // Set up button action for displaying climbers
        displayClimbersButton.setOnAction(event -> handleDisplayClimbers());
    }

    private void handleDisplayClimbers() {
        String experience = experienceField.getText().trim();

        try {
            if (experience.isEmpty()) {
                throw new IllegalArgumentException("Experience level is required.");
            }

            // Display climbers filtered by experience
            StringBuilder displayText = new StringBuilder();
            for (Climber climber : Application.getClimbers()) {
                if (climber.getExperienceLevel().equalsIgnoreCase(experience)) {
                    displayText.append(climber.getFirstName())
                            .append(" ")
                            .append(climber.getLastName())
                            .append("\n");
                }
            }

            if (displayText.isEmpty()) {
                climbersDisplayArea.setText("No climbers available with the specified experience level.");
            } else {
                climbersDisplayArea.setText(displayText.toString());
            }
        } catch (IllegalArgumentException e) {
            climbersDisplayArea.setText("Error: " + e.getMessage());
        }
    }
}
