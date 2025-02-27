package me.ancastanoev.interfaces;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import me.ancastanoev.Application;
import me.ancastanoev.Climber;
import me.ancastanoev.database.ClimberDAO;

public class UserLoginInterface {

    public static void show(Stage stage, Scene mainScene) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f4f4;");

        Label headerLabel = new Label("User Portal");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        HBox headerBox = new HBox(headerLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10, 0, 20, 0));
        root.setTop(headerBox);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter first name");
        formGrid.add(firstNameLabel, 0, 0);
        formGrid.add(firstNameField, 1, 0);

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter last name");
        formGrid.add(lastNameLabel, 0, 1);
        formGrid.add(lastNameField, 1, 1);

        Label contactLabel = new Label("Contact Info:");
        TextField contactField = new TextField();
        contactField.setPromptText("Email or phone number");
        contactField.setVisible(false);
        contactLabel.setVisible(false);
        formGrid.add(contactLabel, 0, 2);
        formGrid.add(contactField, 1, 2);

        Label experienceLabel = new Label("Experience Level:");
        ComboBox<String> experienceBox = new ComboBox<>();
        experienceBox.getItems().addAll("Beginner", "Intermediate", "Expert");
        experienceBox.setPromptText("Select level");
        experienceBox.setVisible(false);
        experienceLabel.setVisible(false);
        formGrid.add(experienceLabel, 0, 3);
        formGrid.add(experienceBox, 1, 3);

        Button actionButton = new Button("Log In");
        actionButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");

        Button toggleButton = new Button("Create Account");
        toggleButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");

        Button backButton = new Button("Back to Main Menu");
        backButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-size: 14px;");

        HBox buttonBox = new HBox(15, actionButton, toggleButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 20, 0));

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefHeight(80);
        resultArea.setStyle("-fx-font-size: 14px;");
        resultArea.setFocusTraversable(false);

        VBox centerBox = new VBox(20, formGrid, buttonBox, resultArea);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        final boolean[] isCreatingAccount = {false};

        toggleButton.setOnAction(e -> {
            isCreatingAccount[0] = !isCreatingAccount[0];
            if (isCreatingAccount[0]) {
                headerLabel.setText("Create New User Account");
                actionButton.setText("Create Account");
                toggleButton.setText("Already Have an Account? Log In");
                contactField.setVisible(true);
                contactLabel.setVisible(true);
                experienceBox.setVisible(true);
                experienceLabel.setVisible(true);
            } else {
                headerLabel.setText("User Portal (No Password)");
                actionButton.setText("Log In");
                toggleButton.setText("Create Account");
                contactField.setVisible(false);
                contactLabel.setVisible(false);
                experienceBox.setVisible(false);
                experienceLabel.setVisible(false);
            }
        });

        actionButton.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                resultArea.setText("Error: First Name and Last Name are required.");
                return;
            }

            ClimberDAO climberDAO = new ClimberDAO();

            if (!isCreatingAccount[0]) {
                Climber matchedClimber = climberDAO.getAllClimbers("user")
                        .stream()
                        .filter(c -> c.getFirstName().equalsIgnoreCase(firstName)
                                && c.getLastName().equalsIgnoreCase(lastName))
                        .findFirst()
                        .orElse(null);

                if (matchedClimber != null) {
                    Application.setLoggedInUser(matchedClimber);
                    Application.setRole("User");
                    resultArea.setText(String.format("Success: Logged in as %s %s (Experience: %s)",
                            matchedClimber.getFirstName(), matchedClimber.getLastName(),
                            matchedClimber.getExperienceLevel()));
                    stage.setScene(mainScene);
                    stage.setFullScreen(true);
                } else {
                    resultArea.setText("Error: No user found with that name.");
                }
            } else {
                String contactInfo = contactField.getText().trim();
                String experienceLevel = experienceBox.getValue();

                if (contactInfo.isEmpty() || experienceLevel == null) {
                    resultArea.setText("Error: Contact Info and Experience Level are required.");
                    return;
                }

                try {
                    Climber newClimber = new Climber(firstName, lastName, experienceLevel, contactInfo);
                    climberDAO.addClimber("user", newClimber);
                    Application.setLoggedInUser(newClimber);
                    Application.setRole("User");
                    resultArea.setText(String.format("Success: Account created! You're now logged in as %s %s (%s).",
                            firstName, lastName, experienceLevel));
                } catch (Exception ex) {
                    resultArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        backButton.setOnAction(e -> {
            stage.setScene(mainScene);
            stage.setFullScreen(true);
        });

        Scene scene = new Scene(root, 500, 500);
        stage.setScene(scene);
        stage.setFullScreen(true);
    }
}
