package me.ancastanoev.client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.util.Optional;
import me.ancastanoev.client.GearClientScene;
public class ExpeditionChatClientFX {

    private VBox messagesBox;
    private ListView<String> membersListView;
    private TextField inputField;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean running = true;
    private String expeditionId;
    private String username;

    private static final String HOST = "localhost";
    private static final int PORT = 23457;


    public static void showChat(String expeditionId) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Your Name");
        dialog.setHeaderText("Welcome to the Expedition Chat");
        dialog.setContentText("Please enter your name:");
        Optional<String> result = dialog.showAndWait();
        String name = (result.isPresent() && !result.get().trim().isEmpty()) ? result.get().trim() : "Guest";
        Stage stage = new Stage();
        ExpeditionChatClientFX chatClient = new ExpeditionChatClientFX();
        chatClient.username = name;
        chatClient.expeditionId = (expeditionId != null && !expeditionId.isEmpty()) ? expeditionId : "Expedition1";
        chatClient.buildAndShow(stage);
    }

    private void buildAndShow(Stage primaryStage) {
        Label titleLabel = new Label("Expedition Chat - " + expeditionId);
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #333;");
        HBox topBox = new HBox(titleLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));
        topBox.setStyle("-fx-background-color: linear-gradient(to right, #e0eafc, #cfdef3);");

        messagesBox = new VBox(10);
        messagesBox.setPadding(new Insets(10));
        messagesBox.setStyle("-fx-background-color: #ffffff;");
        ScrollPane messagesScrollPane = new ScrollPane(messagesBox);
        messagesScrollPane.setFitToWidth(true);
        messagesScrollPane.setPrefHeight(350);
        messagesScrollPane.setStyle("-fx-background: #f7f7f7; -fx-border-color: #ccc;");

        Label membersLabel = new Label("Members");
        membersLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #555;");
        membersListView = new ListView<>();
        membersListView.setPrefWidth(180);
        membersListView.setStyle("-fx-background-color: #fff; -fx-border-color: #ddd;");
        VBox membersBox = new VBox(5, membersLabel, membersListView);
        membersBox.setPadding(new Insets(10));
        membersBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-border-radius: 5;");

        inputField = new TextField();
        inputField.setPromptText("Type your message...");
        inputField.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        Button sendImageButton = new Button("Send Image");
        sendImageButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        Button gearListButton = new Button("Gear List");
        gearListButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        HBox inputButtonsBox = new HBox(10, sendButton, sendImageButton, gearListButton, backButton);
        inputButtonsBox.setAlignment(Pos.CENTER);
        inputButtonsBox.setPadding(new Insets(10));
        VBox bottomBox = new VBox(5, inputField, inputButtonsBox);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setStyle("-fx-background-color: #f0f0f0;");

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(messagesScrollPane);
        root.setRight(membersBox);
        root.setBottom(bottomBox);
        BorderPane.setMargin(membersBox, new Insets(10));

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setTitle("Expedition Group Chat");
        primaryStage.setScene(scene);
        primaryStage.show();

        sendButton.setOnAction(e -> sendTextMessage());
        inputField.setOnAction(e -> sendTextMessage());
        sendImageButton.setOnAction(e -> sendImage());
        gearListButton.setOnAction(e -> {
            // Open the gear list scene (assumes GearClientScene.createScene exists)
            Stage gearStage = new Stage();
            Scene gearScene = GearClientScene.createScene(gearStage, scene);
            gearStage.setScene(gearScene);
            gearStage.setTitle("Gear List");
            gearStage.show();
        });
        backButton.setOnAction(e -> {
            running = false;
            if (out != null) {
                out.println("EXIT");
            }
            primaryStage.close();
        });

        connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            try (Socket socket = new Socket(HOST, PORT)) {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // Send expedition ID and username.
                out.println(expeditionId);
                out.println(username);
                String serverMsg;
                while (running && (serverMsg = in.readLine()) != null) {
                    final String msg = serverMsg;
                    Platform.runLater(() -> processServerMessage(msg));
                }
            } catch (IOException e) {
                Platform.runLater(() -> addSystemMessage("Connection to server lost."));
            }
        }, "ExpeditionChatClientListener").start();
    }


    private void processServerMessage(String msg) {
        if (msg.startsWith("MEMBERS:")) {
            String listStr = msg.substring("MEMBERS:".length());
            String[] members = listStr.split(",");
            membersListView.getItems().setAll(members);
        } else if (msg.contains(": IMG:")) {
            int colonIndex = msg.indexOf(":");
            String sender = msg.substring(0, colonIndex).trim();
            String remaining = msg.substring(colonIndex + 1).trim();
            if (remaining.startsWith("IMG:")) {
                String imageUrl = remaining.substring("IMG:".length()).trim();
                Label senderLabel = new Label(sender + " sent an image:");
                senderLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
                ImageView imageView = new ImageView(new Image(imageUrl, 200, 0, true, true));
                imageView.setOnMouseClicked((MouseEvent e) -> {
                    showEnlargedImage(imageUrl);
                });
                VBox imageBox = new VBox(5, senderLabel, imageView);
                imageBox.setStyle("-fx-background-color: #e8f4ff; -fx-padding: 10; -fx-background-radius: 5;");
                messagesBox.getChildren().add(imageBox);
                return;
            }
        } else {
            Label textLabel = new Label(msg);
            textLabel.setWrapText(true);
            textLabel.setStyle("-fx-background-color: #f1f1f1; -fx-padding: 10; -fx-background-radius: 5;");
            messagesBox.getChildren().add(textLabel);
        }
    }

    private void sendTextMessage() {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty() && out != null) {
            out.println(msg);
            inputField.clear();
        }
    }

    private void sendImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String fileUrl = file.toURI().toString();
            out.println("IMG:" + fileUrl);
        }
    }

    private void addSystemMessage(String message) {
        Label sysLabel = new Label("[System] " + message);
        sysLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
        messagesBox.getChildren().add(sysLabel);
    }

    private void showEnlargedImage(String imageUrl) {
        Stage imageStage = new Stage();
        ImageView imageView = new ImageView(new Image(imageUrl));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(600);
        VBox root = new VBox(imageView);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root);
        imageStage.setScene(scene);
        imageStage.setTitle("Enlarged Image");
        imageStage.show();
    }
}
