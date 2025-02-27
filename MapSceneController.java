package me.ancastanoev.googlemaps;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.net.URL;
import java.util.ResourceBundle;

public class MapSceneController implements Initializable {

    @FXML
    private WebView mapWebView;

    private WebEngine webEngine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webEngine = mapWebView.getEngine();
        URL mapUrl = getClass().getResource("/org/example/demo1/map.html");
        if (mapUrl != null) {
            webEngine.load(mapUrl.toExternalForm());
        } else {
            System.err.println("Error: map.html not found in resources!");
        }
    }


    public void updateMarker(double lat, double lng) {
        if (webEngine != null) {
            String script = String.format("updateMarker(%f, %f)", lat, lng);
            webEngine.executeScript(script);
        }
    }


    public void addMarker(double lat, double lng, String title) {
        if (webEngine != null) {
            String script = String.format("addMarker(%f, %f, '%s')", lat, lng, title);
            webEngine.executeScript(script);
        }
    }


    public void refreshMap() {
        if (webEngine != null) {
            webEngine.executeScript("refreshMap()");
        }
    }
}
