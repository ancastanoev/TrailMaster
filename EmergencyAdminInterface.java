package me.ancastanoev.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class EmergencyAdminInterface extends Application {

    private static final String HOST = "localhost";
    private static final int PORT = 34567;

    private static final CopyOnWriteArrayList<PrintWriter> adminClients = new CopyOnWriteArrayList<>();

    private VBox updatesContainer;
    private volatile boolean running = true;


    public static void showEmergencyAdmin(Stage stage, Scene previousScene) {
        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Admin Password");
        passwordDialog.setHeaderText("Enter Admin Password");
        passwordDialog.setContentText("Password:");
        Optional<String> passwordResult = passwordDialog.showAndWait();

        if (passwordResult.isPresent()) {
            String enteredPassword = passwordResult.get();
            if (!enteredPassword.equals("1234")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect password. Access denied.");
                alert.showAndWait();
                return;
            }
        } else {
            return;
        }

        EmergencyAdminInterface adminInterface = new EmergencyAdminInterface();
        Stage adminStage = new Stage();
        try {
            adminInterface.start(adminStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        updatesContainer = new VBox(10);
        updatesContainer.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(updatesContainer);
        scrollPane.setFitToWidth(true);

        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> {
            running = false;
            primaryStage.close();
        });

        VBox root = new VBox(10, scrollPane, backButton);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Emergency Admin Interface");
        primaryStage.show();

        new Thread(this::connectToServer).start();
    }


    private void connectToServer() {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("ADMIN");

            String line;
            while (running && (line = in.readLine()) != null) {
                final String message = line;
                Platform.runLater(() -> addUpdateNode(message));
            }

        } catch (IOException e) {
            Platform.runLater(() -> {
                Label errorLabel = new Label("Connection error: " + e.getMessage());
                updatesContainer.getChildren().add(errorLabel);
            });
        }
    }


    private void addUpdateNode(String updateMessage) {
        VBox updateBox = new VBox(5);
        updateBox.setPadding(new Insets(5));
        if (updateMessage.startsWith("EMERGENCY:")) {
            updateBox.setStyle("-fx-border-color: red; -fx-border-width: 1; -fx-background-color: #ffe6e6;");
        } else if (updateMessage.startsWith("UPDATE:")) {
            updateBox.setStyle("-fx-border-color: blue; -fx-border-width: 1; -fx-background-color: #e6e6ff;");
        } else {
            updateBox.setStyle("-fx-border-color: gray; -fx-border-width: 1;");
        }
        String[] lines = updateMessage.split("\n");
        for (String line : lines) {
            if (line.trim().toLowerCase().startsWith("image:")) {
                String imageUrl = line.substring("image:".length()).trim();
                try {
                    Image image = new Image(imageUrl, 300, 0, true, true);
                    ImageView imageView = new ImageView(image);
                    updateBox.getChildren().add(imageView);
                } catch (Exception ex) {
                    Label errorLabel = new Label("Could not load image: " + imageUrl);
                    updateBox.getChildren().add(errorLabel);
                }
            } else {
                Label label = new Label(line);
                label.setWrapText(true);
                updateBox.getChildren().add(label);
            }
        }
        updatesContainer.getChildren().add(updateBox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
