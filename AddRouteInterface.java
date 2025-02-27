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
import me.ancastanoev.Route;
import me.ancastanoev.database.MountainDAO;
import me.ancastanoev.database.RouteDAO;

public class AddRouteInterface {

    public static void show(Stage stage, Scene adminScene, String currentRole) {
        // Enforce admin role access
        if (!"admin".equalsIgnoreCase(currentRole)) {
            showAccessDeniedAlert();
            return;
        }

        VBox addRoutePane = new VBox(15);
        addRoutePane.setPadding(new Insets(20));
        addRoutePane.setAlignment(Pos.CENTER);
        addRoutePane.setBackground(new Background(
                new BackgroundFill(Color.LIGHTCYAN, CornerRadii.EMPTY, Insets.EMPTY)));

        Label label = new Label("Add a New Route");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField routeNameField = new TextField();
        routeNameField.setPromptText("Route Name");

        ComboBox<String> mountainComboBox = new ComboBox<>();
        mountainComboBox.setPromptText("Select Mountain");

        // Populate the mountainComboBox from the database
        MountainDAO mountainDAO = new MountainDAO();
        mountainDAO.getAllMountains(currentRole).forEach(
                mountain -> mountainComboBox.getItems().add(mountain.getName())
        );

        ComboBox<String> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("Easy", "Moderate", "Difficult");
        difficultyBox.setPromptText("Difficulty Level");

        Button addRouteButton = new Button("Add Route");
        Button backButton = new Button("Back to Admin");

        addRouteButton.setOnAction(e -> {
            String routeName = routeNameField.getText().trim();
            String mountainName = mountainComboBox.getValue();
            String difficulty = difficultyBox.getValue();

            if (routeName.isEmpty() || mountainName == null || difficulty == null) {
                showMessage("Error", "All fields must be filled out!");
                return;
            }

            try {
                // Input validation
                if (routeName.length() < 3) {
                    throw new IllegalArgumentException("Route name must be at least 3 characters long.");
                }

                // Check for duplicate routes using RouteDAO
                RouteDAO routeDAO = new RouteDAO();
                boolean isDuplicate = routeDAO.getAllRoutes(currentRole).stream()
                        .anyMatch(route -> route.getName().equalsIgnoreCase(routeName)
                                && route.getMountainName().equalsIgnoreCase(mountainName)
                                && route.getDifficultyLevel().equalsIgnoreCase(difficulty));

                if (isDuplicate) {
                    throw new IllegalArgumentException(
                            "A route with the same name, mountain, and difficulty already exists.");
                }

                // Add the new route to the database
                Route newRoute = new Route(routeName, difficulty, mountainName);
                routeDAO.addRoute(currentRole, newRoute);

                showMessage("Success", "Route added successfully!");
                routeNameField.clear();
                mountainComboBox.setValue(null);
                difficultyBox.setValue(null);

            } catch (IllegalArgumentException ex) {
                showMessage("Validation Error", ex.getMessage());
            } catch (Exception ex) {
                showMessage("Unexpected Error", ex.getMessage());
            }
        });

        backButton.setOnAction(e -> stage.setScene(adminScene));

        addRoutePane.getChildren().addAll(label, routeNameField, mountainComboBox, difficultyBox,
                addRouteButton, backButton);
        stage.setScene(new Scene(addRoutePane, 400, 300));
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
