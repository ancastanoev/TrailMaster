package me.ancastanoev.client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GearClientScene {

    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    private static volatile boolean running = true;
    private static PrintWriter out;
    private static BufferedReader in;

    // UI elem.
    public static Scene createScene(Stage stage, Scene previousScene) {
        TextFlow messagesFlow = createMessagesFlow();
        ScrollPane scrollPane = new ScrollPane(messagesFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background: #ecf0f1; -fx-border-color: transparent;");

        TextField inputField = createInputField();
        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: 14px;");
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox inputBox = new HBox(10, inputField, sendButton);
        inputBox.setPadding(new Insets(10));

        VBox root = new VBox(10, scrollPane, inputBox, backButton);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #34495E;");  // Dark blue/grey background

        Scene scene = new Scene(root, 500, 400);

        connectToServer(messagesFlow);

        sendButton.setOnAction(e -> sendMessage(inputField));
        inputField.setOnAction(e -> sendButton.fire());

        backButton.setOnAction(e -> {
            cleanup();
            stage.setScene(previousScene);
        });

        stage.setOnCloseRequest(e -> cleanup());

        return scene;
    }

    private static TextFlow createMessagesFlow() {
        TextFlow flow = new TextFlow();
        flow.setPadding(new Insets(10));
        flow.setLineSpacing(5);
        // Style for the TextFlow container (message area)
        flow.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1px;");
        return flow;
    }


    private static TextField createInputField() {
        TextField textField = new TextField();
        textField.setPromptText("Type a command or message...");
        // Style the input field with white background for clarity
        textField.setStyle("-fx-font-size: 14px; -fx-padding: 5px; " +
                "-fx-background-color: white; -fx-text-fill: black;");
        return textField;
    }


    private static void sendMessage(TextField inputField) {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty() && out != null) {
            out.println(msg);
            inputField.clear();
        }
    }


    private static void cleanup() {
        running = false;
        if (out != null) {
            out.println("EXIT");
        }
    }


    private static void connectToServer(TextFlow messagesFlow) {
        new Thread(() -> {
            try (Socket socket = new Socket(HOST, PORT)) {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String serverMsg;
                while (running && (serverMsg = in.readLine()) != null) {
                    final String msg = serverMsg;
                    Platform.runLater(() -> appendStyledMessage(messagesFlow, msg));
                }
            } catch (IOException e) {
                Platform.runLater(() ->
                        appendStyledMessage(messagesFlow, "Connection to server lost or failed.")
                );
            }
        }, "GearClientListener").start();
    }


    private static void appendStyledMessage(TextFlow messagesFlow, String fullMessage) {
        if (fullMessage.startsWith("[Server]")) {
            Text prefix = new Text("[Server] ");
            prefix.setStyle("-fx-fill: blue; -fx-font-weight: bold; -fx-font-size: 14px;");

            String contentStr = fullMessage.substring(9).trim();

            if (contentStr.startsWith("Commands:")) {
                Text label = new Text("Commands: ");
                label.setStyle("-fx-fill: darkorange; -fx-font-size: 14px;");

                messagesFlow.getChildren().addAll(prefix, label);

                String commandsList = contentStr.substring("Commands:".length()).trim();
                String[] commands = commandsList.split(",");
                for (int i = 0; i < commands.length; i++) {
                    String cmd = commands[i].trim();
                    String style = "-fx-font-size: 14px; ";
                    if (cmd.startsWith("ADD")) {
                        style += "-fx-fill: red;";
                    } else if (cmd.startsWith("REMOVE")) {
                        style += "-fx-fill: blue;";
                    } else if (cmd.startsWith("LIST")) {
                        style += "-fx-fill: green;";
                    } else if (cmd.startsWith("PACK")) {
                        style += "-fx-fill: purple;";
                    } else if (cmd.startsWith("EXIT")) {
                        style += "-fx-fill: orange;";
                    } else {
                        style += "-fx-fill: black;";
                    }
                    Text cmdText = new Text(cmd);
                    cmdText.setStyle(style);
                    messagesFlow.getChildren().add(cmdText);

                    if (i < commands.length - 1) {
                        Text comma = new Text(", ");
                        comma.setStyle("-fx-font-size: 14px; -fx-fill: black;");
                        messagesFlow.getChildren().add(comma);
                    }
                }
                messagesFlow.getChildren().add(new Text("\n"));
            } else {
                Text content = new Text(contentStr);
                if (contentStr.startsWith("Welcome")) {
                    content.setStyle("-fx-fill: green; -fx-font-size: 14px;");
                } else if (contentStr.contains("OK:")) {
                    content.setStyle("-fx-fill: forestgreen; -fx-font-size: 14px;");
                } else if (contentStr.contains("UPDATE:")) {
                    content.setStyle("-fx-fill: purple; -fx-font-size: 14px;");
                } else {
                    content.setStyle("-fx-fill: black; -fx-font-size: 14px;");
                }
                messagesFlow.getChildren().addAll(prefix, content, new Text("\n"));
            }
        } else {
            Text text = new Text(fullMessage + "\n");
            text.setStyle("-fx-fill: black; -fx-font-size: 14px;");
            messagesFlow.getChildren().add(text);
        }
    }
}
