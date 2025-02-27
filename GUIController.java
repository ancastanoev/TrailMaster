package me.ancastanoev;// Example integration of Application logic with GUI

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import me.ancastanoev.io.InputException;

public class GUIController {

    // FXML Components for "Add Mountain" scene
    @FXML
    private TextField mountainNameField;

    @FXML
    private TextField mountainHeightField;

    @FXML
    private Button addMountainButton;

    @FXML
    private TextField climberNameField;

    @FXML
    private TextField climberExperienceField;

    @FXML
    private Button addClimberButton;

    // Initialize method called after FXML is loaded
    @FXML
    public void initialize() {
        addMountainButton.setOnAction(event -> handleAddMountain());
        addClimberButton.setOnAction(event -> handleAddClimber());
    }

    private void handleAddMountain() {
        String mountainName = mountainNameField.getText();
        String mountainHeightText = mountainHeightField.getText();

        try {
            // Validate inputs
            if (mountainName.isEmpty() || mountainHeightText.isEmpty()) {
                throw new InputException("All fields are required.");
            }

            int mountainHeight;
            try {
                mountainHeight = Integer.parseInt(mountainHeightText);
            } catch (NumberFormatException e) {
                throw new InputException("Height must be a valid number.");
            }

            // Create Mountain object and add it via Application logic
            Mountain mountain = new Mountain(mountainName);
           Application.addMountain();

            // Provide feedback to the user (e.g., success alert)
            System.out.println("Mountain added successfully!");

        } catch (InputException e) {
            // Handle validation errors
            System.err.println("Input Error: " + e.getMessage());
        } catch (Exception e) {
            // Handle unexpected errors
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleAddClimber() {
        String climberName = climberNameField.getText();
        String climberExperience = climberExperienceField.getText();

        try {
            // Validate inputs
            if (climberName.isEmpty() || climberExperience.isEmpty()) {
                throw new InputException("All fields are required.");
            }

            // Validate experience level
            if (!Application.isValidExperienceLevel(climberExperience)) {
                throw new InputException("Invalid experience level. Must be Beginner, Intermediate, or Expert.");
            }

            // Create Climber object and add it via Application logic
            Climber climber = new Climber(climberName, climberExperience);
            me.ancastanoev.ClimberManager.addClimber(climber);

            // Provide feedback to the user (e.g., success alert)
            System.out.println("Climber added successfully!");

        } catch (InputException e) {
            // Handle validation errors
            System.err.println("Input Error: " + e.getMessage());
        } catch (Exception e) {
            // Handle unexpected errors
            System.err.println("Error: " + e.getMessage());
        }
    }
}
