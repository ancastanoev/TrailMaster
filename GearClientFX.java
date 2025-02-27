package me.ancastanoev.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class GearClientFX extends Application {

    private TextArea messagesArea;
    private TextField inputField;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean running = true;

    private static final String HOST = "localhost";
    private static final int PORT = 12345;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        messagesArea = new TextArea();
        messagesArea.setEditable(false);
        messagesArea.setWrapText(true);
        messagesArea.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-control-inner-background: #f9f9f9; -fx-border-color: #ccc;");

        inputField = new TextField();
        inputField.setPromptText("Type command (e.g. ADD Tent)...");
        inputField.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-padding: 4;");

        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-background-radius: 5;");

        HBox inputBox = new HBox(10, inputField, sendButton);
        inputBox.setPadding(new Insets(10));
        inputBox.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 5;");
        inputBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox root = new VBox(10, messagesArea, inputBox);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #ffffff;");

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setTitle("Gear Client (JavaFX)");
        primaryStage.setScene(scene);
        primaryStage.show();

        connectToServer();

        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());
    }

    private void connectToServer() {
        new Thread(() -> {
            try (Socket socket = new Socket(HOST, PORT)) {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String serverMsg;
                while (running && (serverMsg = in.readLine()) != null) {
                    final String msg = serverMsg;
                    Platform.runLater(() -> {
                        String displayMsg = msg.contains("packed = flase")
                                ? msg.replace("packed = false", "Not Packed")
                                : msg;
                        messagesArea.appendText("[Server] " + displayMsg + "\n");
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> messagesArea.appendText("Connection to server lost.\n"));
            }
        }, "ServerListener").start();
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (msg.isEmpty() || out == null) {
            return;
        }
        out.println(msg);
        inputField.clear();
    }

    @Override
    public void stop() {
        running = false;
        if (out != null) {
            out.println("EXIT");
        }
    }
}
