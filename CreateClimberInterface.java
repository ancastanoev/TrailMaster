package me.ancastanoev.interfaces;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import me.ancastanoev.Climber;
import me.ancastanoev.database.ClimberDAO;

public class CreateClimberInterface {

    public static void show(Stage stage, Scene adminScene, String currentRole) {
        // Enforce admin role access
        if (!"admin".equalsIgnoreCase(currentRole)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Access Denied");
            alert.setHeaderText(null);
            alert.setContentText("You must be an admin to access this mode.");
            alert.showAndWait();
            return;
        }

        VBox createClimberPane = new VBox(15);
        createClimberPane.setPadding(new Insets(20));
        createClimberPane.setAlignment(Pos.CENTER);
        createClimberPane.setBackground(new Background(
                new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));

        Label label = new Label("Create Climber Profile");
        label.setFont(Font.font("Arial", 16));

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        ComboBox<String> experienceBox = new ComboBox<>();
        experienceBox.getItems().addAll("Beginner", "Intermediate", "Expert");
        experienceBox.setPromptText("Experience Level");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact Info (Email)");

        Button saveButton = new Button("Save Climber");
        Button backButton = new Button("Back to Admin");

        saveButton.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String experience = experienceBox.getValue();
            String contact = contactField.getText().trim();

            if (!validateFields(firstName, lastName, experience, contact)) {
                showMessage("Error", "All fields must be filled out and valid!");
                return;
            }

            try {
                ClimberDAO climberDAO = new ClimberDAO();

                // Check for duplicate climber in the database
                boolean exists = climberDAO.getAllClimbers(currentRole).stream()
                        .anyMatch(c -> c.getFirstName().equalsIgnoreCase(firstName)
                                && c.getLastName().equalsIgnoreCase(lastName)
                                && c.getContactInfo().equalsIgnoreCase(contact));

                if (exists) {
                    showMessage("Error", "A climber with the same name and contact info already exists!");
                    return;
                }

                // Add the new climber to the database
                Climber newClimber = new Climber(firstName, lastName, experience, contact);
                climberDAO.addClimber(currentRole, newClimber);

                showMessage("Success", "Climber profile created successfully!");

                // Clear the input fields
                firstNameField.clear();
                lastNameField.clear();
                experienceBox.setValue(null);
                contactField.clear();

            } catch (Exception ex) {
                showMessage("Error", "An unexpected error occurred: " + ex.getMessage());
            }
        });

        backButton.setOnAction(e -> stage.setScene(adminScene));

        createClimberPane.getChildren().addAll(label, firstNameField, lastNameField,
                experienceBox, contactField, saveButton, backButton);
        stage.setScene(new Scene(createClimberPane, 400, 400));
    }

    // Validate input fields
    private static boolean validateFields(String firstName, String lastName,
                                          String experience, String contact) {
        if (firstName.isEmpty() || lastName.isEmpty()
                || experience == null || contact.isEmpty()) {
            return false;
        }
        // Validate email format (simple regex for demo purposes)
        if (!contact.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return false;
        }
        return true;
    }

    // Show alert message
    private static void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
