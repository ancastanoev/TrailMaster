package me.ancastanoev.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import me.ancastanoev.profile.ClimberProfile;
import me.ancastanoev.profile.ProfileManager;
import me.ancastanoev.Application;
import me.ancastanoev.Climber;
import me.ancastanoev.interfaces.ClimberAppInterface;

import java.io.File;
import java.util.List;

public class ClimberProfileInterface {


    public static void showProfile(Stage primaryStage, Scene previousScene) {
        Climber currentUser = Application.getLoggedInUser();
        if (currentUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You must be logged in to view your profile.");
            alert.showAndWait();
            return;
        }

        final String userKey = currentUser.getFirstName().trim() + "_" + currentUser.getLastName().trim();

        ClimberProfile tempProfile = ProfileManager.loadProfile(userKey);
        if (tempProfile == null) {
            tempProfile = new ClimberProfile(
                    currentUser.getFirstName(),
                    currentUser.getLastName(),
                    currentUser.getExperienceLevel(),
                    currentUser.getContactInfo()
            );
        }
        final ClimberProfile profile = tempProfile;

        Label titleLabel = new Label("My Profile");
        titleLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #ffffff; -fx-background-color: #2c3e50; -fx-padding: 20px;");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        ImageView profileImageView = new ImageView();
        profileImageView.setFitWidth(300);
        profileImageView.setFitHeight(300);
        profileImageView.setPreserveRatio(true);
        profileImageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0.5, 0, 0);");
        if (profile.getProfilePictureUrl() != null && !profile.getProfilePictureUrl().isEmpty()) {
            profileImageView.setImage(new Image(profile.getProfilePictureUrl(), 300, 300, true, true));
        } else {
            profileImageView.setImage(new Image("https://via.placeholder.com/300"));
        }

        Button uploadPicButton = new Button("Upload Picture");
        uploadPicButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        uploadPicButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Profile Picture");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                String fileUrl = file.toURI().toString();
                profile.setProfilePictureUrl(fileUrl);
                profileImageView.setImage(new Image(fileUrl, 300, 300, true, true));
            }
        });

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(10);
        detailsGrid.setPadding(new Insets(20));
        detailsGrid.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");

        Label firstNameLabel = new Label("First Name:");
        firstNameLabel.setStyle("-fx-font-weight: bold;");
        TextField firstNameField = new TextField(profile.getFirstName());

        Label lastNameLabel = new Label("Last Name:");
        lastNameLabel.setStyle("-fx-font-weight: bold;");
        TextField lastNameField = new TextField(profile.getLastName());

        Label experienceLabel = new Label("Experience:");
        experienceLabel.setStyle("-fx-font-weight: bold;");
        TextField experienceField = new TextField(profile.getExperienceLevel());

        Label contactLabel = new Label("Contact Info:");
        contactLabel.setStyle("-fx-font-weight: bold;");
        TextField contactField = new TextField(profile.getContactInfo());

        Label bioLabel = new Label("Bio:");
        bioLabel.setStyle("-fx-font-weight: bold;");
        TextArea bioArea = new TextArea(profile.getBio());
        bioArea.setPrefRowCount(4);
        bioArea.setWrapText(true);
        bioArea.setStyle("-fx-background-radius: 5;");

        detailsGrid.add(firstNameLabel, 0, 0);
        detailsGrid.add(firstNameField, 1, 0);
        detailsGrid.add(lastNameLabel, 0, 1);
        detailsGrid.add(lastNameField, 1, 1);
        detailsGrid.add(experienceLabel, 0, 2);
        detailsGrid.add(experienceField, 1, 2);
        detailsGrid.add(contactLabel, 0, 3);
        detailsGrid.add(contactField, 1, 3);
        detailsGrid.add(bioLabel, 0, 4);
        detailsGrid.add(bioArea, 1, 4);

        Label expeditionsLabel = new Label("Planned Expeditions:");
        expeditionsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        ListView<String> expeditionsList = new ListView<>();
        List<ClimberAppInterface.expedition> plannedExps = ClimberAppInterface.getPlannedExpeditions();
        if (plannedExps != null && !plannedExps.isEmpty()) {
            for (ClimberAppInterface.expedition exp : plannedExps) {
                expeditionsList.getItems().add(exp.name);
            }
        } else {
            expeditionsList.getItems().add("No planned expeditions.");
        }
        expeditionsList.setPrefHeight(100);
        expeditionsList.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 5;");

        Button saveButton = new Button("Save Profile");
        saveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setOnAction(e -> {
            profile.setFirstName(firstNameField.getText().trim());
            profile.setLastName(lastNameField.getText().trim());
            profile.setExperienceLevel(experienceField.getText().trim());
            profile.setContactInfo(contactField.getText().trim());
            profile.setBio(bioArea.getText().trim());
            ProfileManager.saveProfile(userKey, profile);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profile saved successfully!");
            alert.showAndWait();
        });

        Button backButton = new Button("Back to Main Menu");
        backButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(previousScene));

        HBox buttonBox = new HBox(20, saveButton, backButton);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setAlignment(Pos.CENTER);

        VBox leftBox = new VBox(15, profileImageView, uploadPicButton);
        leftBox.setPadding(new Insets(20));
        leftBox.setAlignment(Pos.TOP_CENTER);


        VBox rightBox = new VBox(20, detailsGrid, expeditionsLabel, expeditionsList);
        rightBox.setPadding(new Insets(20));


        BorderPane mainRoot = new BorderPane();
        mainRoot.setTop(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        mainRoot.setLeft(leftBox);
        mainRoot.setCenter(rightBox);
        mainRoot.setBottom(buttonBox);
        mainRoot.setStyle("-fx-background-color: #bdc3c7; -fx-padding: 10;");

        Scene scene = new Scene(mainRoot, 800, 600);
        primaryStage.setTitle("My Profile");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
