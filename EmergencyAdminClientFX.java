package me.ancastanoev.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class EmergencyAdminClientFX extends Application {
    private TextArea textArea;
    private volatile boolean running = true;
    private static final String HOST = "localhost";
    private static final int PORT = 34567;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        textArea = new TextArea();
        textArea.setEditable(false);

        VBox root = new VBox(10, textArea);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Emergency Admin Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(this::connectToServer, "EmergencyAdminClient-Listener").start();
    }

    private void connectToServer() {
        try (Socket socket = new Socket(HOST, PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("ADMIN");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while (running && (line = in.readLine()) != null) {
                final String message = line;
                Platform.runLater(() -> textArea.appendText(message + "\n"));
            }
        } catch (IOException e) {
            Platform.runLater(() -> textArea.appendText("Connection to emergency server lost.\n"));
        }
    }

    @Override
    public void stop() {
        running = false;
    }
}
