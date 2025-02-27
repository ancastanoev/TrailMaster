package me.ancastanoev.interfaces;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.ancastanoev.imageapi.ImageService;

import java.io.IOException;
import java.util.List;

public class ImageGalleryInterface {


    public static void show(Stage stage, Scene previousScene, String query) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.setScene(previousScene));

        FlowPane imagesPane = new FlowPane();
        imagesPane.setHgap(10);
        imagesPane.setVgap(10);
        imagesPane.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(imagesPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);

        root.getChildren().addAll(backButton, scrollPane);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);

        // Fetch images off the JavaFX Application thread
        new Thread(() -> {
            ImageService imageService = new ImageService();
            try {
                List<String> imageUrls = imageService.getImageUrls(query);
                Platform.runLater(() -> {
                    for (String url : imageUrls) {
                        Image image = new Image(url, 200, 0, true, true);
                        ImageView imageView = new ImageView(image);
                        imagesPane.getChildren().add(imageView);
                    }
                });
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
