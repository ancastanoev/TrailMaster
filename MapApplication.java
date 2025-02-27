package me.ancastanoev.googlemaps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MapApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo1/MapScene.fxml"));

            AnchorPane root = loader.load();
            Scene scene = new Scene(root, 800, 600);

            primaryStage.setTitle("Hiking Trail Map");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading MapScene.fxml");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
