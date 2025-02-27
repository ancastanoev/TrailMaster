package me.ancastanoev.interfaces;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.ancastanoev.Climber;
import me.ancastanoev.database.ClimberDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayClimbersByExperienceInterface {

    public static void show(Stage stage, Scene adminScene, String currentRole) {
        VBox displayClimbersPane = new VBox(15);
        displayClimbersPane.setPadding(new Insets(20));
        displayClimbersPane.setAlignment(Pos.CENTER);
        displayClimbersPane.setBackground(new Background(
                new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        Label label = new Label("Display Climbers by Experience");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextArea climbersArea = new TextArea();
        climbersArea.setEditable(false);
        climbersArea.setPrefHeight(300);
        climbersArea.setWrapText(true);

        Button displayButton = new Button("Display Climbers");
        Button backButton = new Button("Back to Admin");

        displayButton.setOnAction(e -> {
            if (!currentRole.equalsIgnoreCase("admin")) {
                climbersArea.setText("Permission denied: Only admins can view climbers.");
                return;
            }
            try {
                // Load climbers from the database
                ClimberDAO climberDAO = new ClimberDAO();
                List<Climber> climbers = climberDAO.getAllClimbers(currentRole);

                // Group climbers by experience level
                Map<String, List<Climber>> groupedClimbers = groupClimbersByExperience(climbers);

                // Prepare the output for display
                StringBuilder result = new StringBuilder();
                for (Map.Entry<String, List<Climber>> entry : groupedClimbers.entrySet()) {
                    result.append("Experience Level: ").append(entry.getKey()).append("\n");
                    for (Climber climber : entry.getValue()) {
                        result.append("  - ")
                                .append(climber.getFirstName()).append(" ")
                                .append(climber.getLastName())
                                .append("\n");
                    }
                    result.append("\n");
                }

                if (result.isEmpty()) {
                    result.append("No climbers available.");
                }

                climbersArea.setText(result.toString());

            } catch (Exception ex) {
                climbersArea.setText("An error occurred while fetching the climbers: " + ex.getMessage());
            }
        });

        backButton.setOnAction(e -> stage.setScene(adminScene));

        displayClimbersPane.getChildren().addAll(label, climbersArea, displayButton, backButton);
        stage.setScene(new Scene(displayClimbersPane, 500, 400));
    }

    private static Map<String, List<Climber>> groupClimbersByExperience(List<Climber> climbers) {
        Map<String, List<Climber>> groupedClimbers = new HashMap<>();

        for (Climber climber : climbers) {
            String experience = climber.getExperienceLevel();
            groupedClimbers
                    .computeIfAbsent(experience, k -> new ArrayList<>())
                    .add(climber);
        }

        return groupedClimbers;
    }
}
