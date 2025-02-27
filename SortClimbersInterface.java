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

import java.util.Comparator;
import java.util.List;

public class SortClimbersInterface {

    public static void show(Stage stage, Scene adminScene, String currentRole) {
        VBox sortClimbersPane = new VBox(15);
        sortClimbersPane.setPadding(new Insets(20));
        sortClimbersPane.setAlignment(Pos.CENTER);
        sortClimbersPane.setBackground(new Background(
                new BackgroundFill(Color.LIGHTCYAN, CornerRadii.EMPTY, Insets.EMPTY)));

        Label label = new Label("Sort Climbers");
        label.setFont(Font.font("Arial", 16));

        TextArea sortedClimbersArea = new TextArea();
        sortedClimbersArea.setEditable(false);
        sortedClimbersArea.setPrefHeight(200);

        Button sortButton = new Button("Sort Climbers");
        Button backButton = new Button("Back to Admin");

        sortButton.setOnAction(e -> {
            try {
                ClimberDAO climberDAO = new ClimberDAO();
                List<Climber> climbers = climberDAO.getAllClimbers(currentRole);

                // Sort climbers by experience level and then by name
                climbers.sort(Comparator
                        .comparing(Climber::getExperienceLevel)
                        .thenComparing(Climber::getFirstName)
                        .thenComparing(Climber::getLastName));

                // Display sorted climbers in the TextArea
                StringBuilder sortedClimbers = new StringBuilder("Sorted Climbers:\n");
                for (Climber climber : climbers) {
                    sortedClimbers.append(climber.getFirstName()).append(" ")
                            .append(climber.getLastName()).append(" - ")
                            .append(climber.getExperienceLevel()).append("\n");
                }
                sortedClimbersArea.setText(sortedClimbers.toString());

            } catch (SecurityException secEx) {
                sortedClimbersArea.setText("Access Denied: " + secEx.getMessage());
            } catch (Exception ex) {
                sortedClimbersArea.setText("Unexpected Error: " + ex.getMessage());
            }
        });

        backButton.setOnAction(e -> stage.setScene(adminScene));

        sortClimbersPane.getChildren().addAll(label, sortedClimbersArea, sortButton, backButton);
        stage.setScene(new Scene(sortClimbersPane, 500, 400));
    }
}
