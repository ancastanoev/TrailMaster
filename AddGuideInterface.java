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
import me.ancastanoev.Guide;
import me.ancastanoev.database.GuideDAO;

import java.util.List;

public class AddGuideInterface {

    public static void show(Stage stage, Scene adminScene, String currentRole) {
        // Enforce admin role access
        if (!"admin".equalsIgnoreCase(currentRole)) {
            showAccessDeniedAlert();
            return;
        }

        // Set up the UI layout
        VBox addGuidePane = new VBox(15);
        addGuidePane.setPadding(new Insets(20));
        addGuidePane.setAlignment(Pos.CENTER);
        addGuidePane.setBackground(new Background(
                new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));

        Label label = new Label("Add a New Guide");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField guideNameField = new TextField();
        guideNameField.setPromptText("Guide Name");

        ComboBox<String> experienceBox = new ComboBox<>();
        experienceBox.getItems().addAll("Beginner", "Intermediate", "Expert");
        experienceBox.setPromptText("Experience Level");

        Button addGuideButton = new Button("Add Guide");
        Button backButton = new Button("Back to Admin");

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefHeight(200);

        GuideDAO guideDAO = new GuideDAO(); // DAO instance for database interaction

        addGuideButton.setOnAction(e -> {
            String guideName = guideNameField.getText().trim();
            String experience = experienceBox.getValue();

            if (guideName.isEmpty() || experience == null) {
                resultArea.setText("Error: All fields must be filled out!");
                return;
            }

            try {
                // Input Validation
                if (guideName.length() < 3) {
                    throw new IllegalArgumentException("Guide name must be at least 3 characters long.");
                }

                // Check for duplicates in the database
                List<Guide> existingGuides = guideDAO.getAllGuides(currentRole);
                boolean isDuplicate = existingGuides.stream()
                        .anyMatch(guide -> guide.getName().equalsIgnoreCase(guideName)
                                && guide.getExperienceLevel().equalsIgnoreCase(experience));

                if (isDuplicate) {
                    throw new IllegalArgumentException("A guide with the same name and experience level already exists in the database.");
                }

                // Add the new guide to the database
                Guide newGuide = new Guide(guideName, experience);
                guideDAO.addGuide(currentRole, newGuide);

                resultArea.setText("Success: Guide added successfully!");
                guideNameField.clear();
                experienceBox.setValue(null);

            } catch (IllegalArgumentException ex) {
                resultArea.setText("Validation Error: " + ex.getMessage());
            } catch (Exception ex) {
                resultArea.setText("Unexpected Error: " + ex.getMessage());
            }
        });

        backButton.setOnAction(e -> stage.setScene(adminScene));

        addGuidePane.getChildren().addAll(label, guideNameField, experienceBox, addGuideButton, resultArea, backButton);
        stage.setScene(new Scene(addGuidePane, 400, 400));
    }

    private static void showAccessDeniedAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Access Denied");
        alert.setHeaderText(null);
        alert.setContentText("You must be an admin to access this mode.");
        alert.showAndWait();
    }
}
