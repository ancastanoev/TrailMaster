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
import me.ancastanoev.Mountain;
import me.ancastanoev.database.MountainDAO;

public class AddMountainInterface {

    public static void show(Stage stage, Scene adminScene, String currentRole) {
        // Enforce admin role access
        if (!"admin".equalsIgnoreCase(currentRole)) {
            showAccessDeniedAlert();
            return;
        }

        VBox addMountainPane = new VBox(15);
        addMountainPane.setPadding(new Insets(20));
        addMountainPane.setAlignment(Pos.CENTER);
        addMountainPane.setBackground(new Background(
                new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));

        Label addMountainLabel = new Label("Add Mountain");
        addMountainLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField mountainNameField = new TextField();
        mountainNameField.setPromptText("Mountain Name");

        Button saveMountainButton = new Button("Save Mountain");
        Button backButton = new Button("Back to Admin");

        saveMountainButton.setOnAction(e -> {
            String mountainName = mountainNameField.getText().trim();
            if (mountainName.isEmpty()) {
                showMessage("Error", "Mountain name cannot be empty!");
                return;
            }

            try {
                // Input validation
                if (mountainName.length() < 3) {
                    throw new IllegalArgumentException("Mountain name must be at least 3 characters long.");
                }

                // Check for duplicates using MountainDAO
                MountainDAO mountainDAO = new MountainDAO();
                boolean isDuplicate = mountainDAO.getAllMountains(currentRole).stream()
                        .anyMatch(mountain -> mountain.getName().equalsIgnoreCase(mountainName));

                if (isDuplicate) {
                    throw new IllegalArgumentException("A mountain with the same name already exists.");
                }

                // Add the new mountain to the database
                Mountain newMountain = new Mountain(mountainName);
                mountainDAO.addMountain(currentRole, newMountain);

                showMessage("Success", "Mountain added successfully!");
                mountainNameField.clear();

            } catch (IllegalArgumentException ex) {
                showMessage("Validation Error", ex.getMessage());
            } catch (Exception ex) {
                showMessage("Unexpected Error", ex.getMessage());
            }
        });

        backButton.setOnAction(e -> stage.setScene(adminScene));

        addMountainPane.getChildren().addAll(addMountainLabel, mountainNameField, saveMountainButton, backButton);
        stage.setScene(new Scene(addMountainPane, 400, 300));
    }

    private static void showAccessDeniedAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Access Denied");
        alert.setHeaderText(null);
        alert.setContentText("You must be an admin to access this mode.");
        alert.showAndWait();
    }

    private static void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
