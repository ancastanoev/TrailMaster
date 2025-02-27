package me.ancastanoev.interfaces;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import me.ancastanoev.database.ClimberDAO;

public class ChangeExperienceInterface {

    public static void show(Stage stage, Scene adminScene, String currentRole) {
        // Enforce admin role access
        if (!"admin".equalsIgnoreCase(currentRole)) {
            showAccessDeniedAlert();
            return;
        }

        VBox changeExperiencePane = new VBox(15);
        changeExperiencePane.setPadding(new Insets(20));
        changeExperiencePane.setAlignment(Pos.CENTER);
        changeExperiencePane.setBackground(new Background(
                new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));

        Label label = new Label("Change Climber Experience Level");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        ComboBox<String> experienceBox = new ComboBox<>();
        experienceBox.getItems().addAll("Beginner", "Intermediate", "Expert");
        experienceBox.setPromptText("New Experience Level");

        Button changeButton = new Button("Change Experience Level");
        Button backButton = new Button("Back to Admin");

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefHeight(200);

        changeButton.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String newExperience = experienceBox.getValue();

            if (firstName.isEmpty() || lastName.isEmpty() || newExperience == null) {
                resultArea.setText("Error: All fields must be filled out!");
                return;
            }

            ClimberDAO climberDAO = new ClimberDAO();

            try {
                // Attempt to update the climber's experience level
                if (climberDAO.updateExperienceLevel(currentRole, firstName, lastName, newExperience)) {
                    resultArea.setText("Success: Experience level updated to " + newExperience +
                            " for climber " + firstName + " " + lastName);
                    firstNameField.clear();
                    lastNameField.clear();
                    experienceBox.setValue(null);
                } else {
                    resultArea.setText("Error: Climber not found or update failed.");
                }
            } catch (SecurityException ex) {
                resultArea.setText("Access Denied: " + ex.getMessage());
            } catch (Exception ex) {
                resultArea.setText("Unexpected Error: " + ex.getMessage());
            }
        });

        backButton.setOnAction(e -> stage.setScene(adminScene));

        changeExperiencePane.getChildren().addAll(label, firstNameField, lastNameField,
                experienceBox, changeButton, resultArea, backButton);
        stage.setScene(new Scene(changeExperiencePane, 500, 400));
    }

    private static void showAccessDeniedAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Access Denied");
        alert.setHeaderText(null);
        alert.setContentText("You must be an admin to access this mode.");
        alert.showAndWait();
    }
}
