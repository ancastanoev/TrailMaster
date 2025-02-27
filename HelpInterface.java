package me.ancastanoev.interfaces;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HelpInterface {

    public static void show(Stage stage, Scene mainScene) {
        // Create a vertical container for the help content.
        VBox container = new VBox(15);
        container.setPadding(new javafx.geometry.Insets(20));
        container.setAlignment(javafx.geometry.Pos.CENTER);
        // Set a background color or gradient (here we use a solid light coral color)
        container.setBackground(new Background(new BackgroundFill(Color.LIGHTCORAL, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));

        // Title label
        Label titleLabel = new Label("Help & Instructions");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);

        // Create a TextArea to hold the detailed help text.
        TextArea helpText = new TextArea();
        helpText.setEditable(false);
        helpText.setWrapText(true);
        helpText.setFont(Font.font("Arial", 14));
        helpText.setText(
                "Welcome to the Climber Expedition Manager Application!\n\n" +
                        "This application helps climbers and administrators manage expeditions effectively. " +
                        "Users can log in or create new accounts, plan expeditions, join group chats, and report live updates " +
                        "from their climbing adventures.\n\n" +
                        "Features:\n" +
                        "1. User Mode: Log in or create a new account to access your personalized expedition dashboard. " +
                        "View your profile, update your gear list, and join expedition group chats.\n\n" +
                        "2. Admin Mode: Administrators can manage climber profiles, add new mountains and routes, " +
                        "and view overall expedition statistics and emergency signals.\n\n" +
                        "3. Emergency Admin: Monitor live emergency updates sent by users during expeditions, " +
                        "including location, timestamp, and any attached images. This helps in coordinating rescue efforts " +
                        "and ensuring climber safety.\n\n" +
                        "4. Gear Client: Access and update a shared gear list to ensure all climbers are properly equipped.\n\n" +
                        "5. Expedition Reporting: Send live updates (with text and images) and view interactive maps " +
                        "showing your route and progress.\n\n" +
                        "Usage Instructions:\n" +
                        "- Use the navigation buttons on the main menu to switch between User, Admin, and Emergency Admin modes.\n" +
                        "- In User Mode, log in to view your profile on the sidebar, where your name and profile picture will appear.\n" +
                        "- In Admin Mode, manage climbers, routes, and mountains.\n" +
                        "- In Emergency Admin mode, monitor real‑time emergency signals from users.\n\n" +
                        "For more detailed documentation, please refer to the user manual provided with the application.\n\n" +
                        "Happy Climbing!"
        );

        // Place the TextArea inside a ScrollPane to support long help text.
        ScrollPane scrollPane = new ScrollPane(helpText);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        // Back button to return to the main menu.
        Button backButton = new Button("Back to Main");
        backButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        backButton.setTextFill(Color.WHITE);
        // Here we set a dark background for contrast.
        backButton.setStyle("-fx-background-color: #333333; -fx-cursor: hand; -fx-padding: 8 15;");
        backButton.setOnAction(e -> stage.setScene(mainScene));

        // Add all nodes to the container.
        container.getChildren().addAll(titleLabel, scrollPane, backButton);

        // Create the scene for the HelpInterface and set it on the stage.
        Scene helpScene = new Scene(container, 600, 400);
        stage.setScene(helpScene);
    }
}
