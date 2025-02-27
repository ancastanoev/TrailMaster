package me.ancastanoev.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.CornerRadii;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.ancastanoev.imageapi.ImageService;
import netscape.javascript.JSObject;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URLEncoder;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ExpeditionReportingInterface extends Application {

    private TextArea updateFeed;
    private TextField updateTextField;
    private Button uploadImageButton;
    private Button sendUpdateButton;
    private Button emergencyButton;
    private Label statusLabel;

    private File selectedImageFile;

    // WebView for the map
    private WebView mapView;
    private WebEngine webEngine;

    private final List<ExpeditionUpdate> updates = new ArrayList<>();

    private String currentLocation = fetchLiveLocation();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initializeDummyExpeditions();

        BorderPane root = new BorderPane();

        Label title = new Label("Expedition Live Reporting");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        HBox topBar = new HBox(title);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10));
        root.setTop(topBar);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);

        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));

        updateFeed = new TextArea();
        updateFeed.setEditable(false);
        updateFeed.setWrapText(true);
        updateFeed.setPrefHeight(300);

        updateTextField = new TextField();
        updateTextField.setPromptText("Enter your update here...");
        updateTextField.setPrefWidth(300);

        uploadImageButton = new Button("Upload Image");
        uploadImageButton.setOnAction(e -> handleImageUpload());

        sendUpdateButton = new Button("Send Update");
        sendUpdateButton.setOnAction(e -> sendUpdate());

        emergencyButton = new Button("Emergency");
        emergencyButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        emergencyButton.setOnAction(e -> sendEmergencySignal());

        HBox controlButtons = new HBox(10, uploadImageButton, sendUpdateButton, emergencyButton);
        controlButtons.setAlignment(Pos.CENTER);

        leftPane.getChildren().addAll(new Label("Live Updates:"), updateFeed, updateTextField, controlButtons);

        mapView = new WebView();
        mapView.setPrefSize(400, 400);
        webEngine = mapView.getEngine();
        URL mapUrl = getClass().getResource("/org/example/demo1/map.html");
        if (mapUrl != null) {
            webEngine.load(mapUrl.toExternalForm());
        } else {
            System.err.println("Error: map.html not found!");
        }
        webEngine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
            if (newDoc != null) {
                Platform.runLater(() -> {
                    webEngine.executeScript("refreshMap()");
                    webEngine.executeScript("map.setView([45.7597,21.2300],13);");
                    webEngine.executeScript("setHikerIcon()");
                });
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", new JavaConnector());
            }
        });
        VBox rightPane = new VBox(mapView);
        rightPane.setPadding(new Insets(10));

        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.6);
        root.setCenter(splitPane);

        HBox statusPane = new HBox(20);
        statusPane.setPadding(new Insets(10));
        statusPane.setAlignment(Pos.CENTER);
        Label locationLabel = new Label("Location: " + getCurrentLocation());
        Label timestampLabel = new Label("Timestamp: " + getCurrentTimestamp());
        statusLabel = new Label("Status: Ready");
        statusPane.getChildren().addAll(locationLabel, timestampLabel, statusLabel);
        root.setBottom(statusPane);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Expedition Reporting");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public class JavaConnector {
        public void updateLocation(double lat, double lon) {
            currentLocation = lat + ", " + lon;
            System.out.println("Updated location from geolocation: " + currentLocation);
        }
    }




    private String fetchLiveLocation() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://ip-api.com/json"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = new Gson().fromJson(response.body(), JsonObject.class);
            if ("success".equalsIgnoreCase(json.get("status").getAsString())) {
                double lat = json.get("lat").getAsDouble();
                double lon = json.get("lon").getAsDouble();
                return lat + ", " + lon;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "45.7597, 21.2300";
    }

    private String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            statusLabel.setText("Image selected: " + selectedImageFile.getName());
        } else {
            statusLabel.setText("No image selected.");
        }
    }

    private String getCurrentLocation() {
        return (currentLocation != null) ? currentLocation : "45.7597, 21.2300";
    }
    private void sendUpdate() {
        String text = updateTextField.getText().trim();
        if (text.isEmpty() && selectedImageFile == null) {
            showAlert("Error", "Please enter some text or select an image for the update.");
            return;
        }
        String location = getCurrentLocation();
        String timestamp = getCurrentTimestamp();
        String updateMessage = "UPDATE: [" + timestamp + "] (" + location + ") " + text;
        if (selectedImageFile != null) {
            try {
                byte[] imageBytes = Files.readAllBytes(selectedImageFile.toPath());
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                String mimeType = "image/png"; // default
                String fileName = selectedImageFile.getName().toLowerCase();
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    mimeType = "image/jpeg";
                } else if (fileName.endsWith(".gif")) {
                    mimeType = "image/gif";
                }
                String dataUri = "data:" + mimeType + ";base64," + base64Image;
                updateMessage += "\nImage: " + dataUri;
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert("Error", "Failed to read image file."));
                return;
            }
        }
        String finalUpdateMessage = updateMessage;
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 34567);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out.println(finalUpdateMessage);
                String response = in.readLine();
                System.out.println("Server response: " + response);
            } catch (IOException ex) {
                ex.printStackTrace();
                Platform.runLater(() -> showAlert("Error", "Failed to send update: " + ex.getMessage()));
            }
            Platform.runLater(() -> {
                updateFeed.appendText(finalUpdateMessage + "\n--------------------------------------------------\n");
                updateTextField.clear();
                selectedImageFile = null;
                statusLabel.setText("Update sent at " + timestamp);
            });
        }).start();
    }


    private void sendEmergencySignal() {
        Platform.runLater(() -> webEngine.executeScript("map.setView([45.7597,21.2300],13);"));
        String locationStr = getCurrentLocation();
        String timestamp = getCurrentTimestamp();
        String emergencyMessage = "EMERGENCY: Location: " + locationStr + ", Timestamp: " + timestamp;
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 34567);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out.println(emergencyMessage);
                String response = in.readLine();
                System.out.println("Server response: " + response);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Platform.runLater(() -> {
                showAlert("Emergency", "Emergency signal sent:\n" + emergencyMessage);
                updateFeed.appendText("[" + timestamp + "] EMERGENCY: " + emergencyMessage + "\n");
                statusLabel.setText("Emergency signal sent at " + timestamp);
            });
        }).start();
    }

    private double[] parseCoordinates(String coordStr) {
        String[] parts = coordStr.split(",");
        double lat = 0, lon = 0;
        if (parts.length >= 2) {
            try {
                lat = Double.parseDouble(parts[0].trim());
                lon = Double.parseDouble(parts[1].trim());
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
        return new double[]{lat, lon};
    }


    private Refuge getNearestRefugeFromAPI(double currentLat, double currentLon) {
        String query = "[out:json];node(around:5000," + currentLat + "," + currentLon + ")[tourism=alpine_hut];out;";
        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = "http://overpass-api.de/api/interpreter?data=" +
                    URLEncoder.encode(query, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = new Gson().fromJson(response.body(), JsonObject.class);
            JsonArray elements = json.getAsJsonArray("elements");
            if (elements == null || elements.size() == 0) {
                return null;
            }
            Refuge nearest = null;
            double minDistance = Double.MAX_VALUE;
            for (int i = 0; i < elements.size(); i++) {
                JsonObject element = elements.get(i).getAsJsonObject();
                double lat = element.get("lat").getAsDouble();
                double lon = element.get("lon").getAsDouble();
                String name = "Unknown Refuge";
                if (element.has("tags") && element.getAsJsonObject("tags").has("name")) {
                    name = element.getAsJsonObject("tags").get("name").getAsString();
                }
                double distance = haversine(currentLat, currentLon, lat, lon);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = new Refuge(name, lat, lon);
                }
            }
            return nearest;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km.
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ------------------ Map Route and Image Utilities ------------------

    private void searchAndDisplayRoute(String startPlace, String endPlace, long unusedDuration, WebEngine webEngine) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String startQuery = URLEncoder.encode(startPlace, StandardCharsets.UTF_8);
            HttpRequest startRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://nominatim.openstreetmap.org/search?format=json&q=" + startQuery))
                    .header("User-Agent", "JavaFXApp")
                    .build();
            HttpResponse<String> startResponse = client.send(startRequest, HttpResponse.BodyHandlers.ofString());
            JsonArray startResults = new Gson().fromJson(startResponse.body(), JsonArray.class);
            if (startResults.size() == 0) {
                showAlert("Error", "Start location not found.");
                return;
            }
            JsonObject startObj = startResults.get(0).getAsJsonObject();
            double startLat = Double.parseDouble(startObj.get("lat").getAsString());
            double startLon = Double.parseDouble(startObj.get("lon").getAsString());
            String endQuery = URLEncoder.encode(endPlace, StandardCharsets.UTF_8);
            HttpRequest endRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://nominatim.openstreetmap.org/search?format=json&q=" + endQuery))
                    .header("User-Agent", "JavaFXApp")
                    .build();
            HttpResponse<String> endResponse = client.send(endRequest, HttpResponse.BodyHandlers.ofString());
            JsonArray endResults = new Gson().fromJson(endResponse.body(), JsonArray.class);
            if (endResults.size() == 0) {
                showAlert("Error", "End location not found.");
                return;
            }
            JsonObject endObj = endResults.get(0).getAsJsonObject();
            double endLat = Double.parseDouble(endObj.get("lat").getAsString());
            double endLon = Double.parseDouble(endObj.get("lon").getAsString());
            String osrmUrl = String.format("http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                    startLon, startLat, endLon, endLat);
            HttpRequest osrmRequest = HttpRequest.newBuilder().uri(URI.create(osrmUrl)).build();
            HttpResponse<String> osrmResponse = client.send(osrmRequest, HttpResponse.BodyHandlers.ofString());
            JsonObject osrmJson = new Gson().fromJson(osrmResponse.body(), JsonObject.class);
            JsonArray routes = osrmJson.getAsJsonArray("routes");
            if (routes.size() == 0) {
                showAlert("Error", "No route found.");
                return;
            }
            JsonObject routeObj = routes.get(0).getAsJsonObject();
            double osrmDurationSec = routeObj.get("duration").getAsDouble();
            int computedDurationHours = (int) Math.round(osrmDurationSec / 3600.0);
            JsonObject geometry = routeObj.getAsJsonObject("geometry");
            JsonArray coordinates = geometry.getAsJsonArray("coordinates");
            StringBuilder jsArrayBuilder = new StringBuilder("[");
            jsArrayBuilder.append("[").append(startLat).append(",").append(startLon).append("]");
            for (int i = 1; i < coordinates.size() - 1; i++) {
                JsonArray coordPair = coordinates.get(i).getAsJsonArray();
                double lon = coordPair.get(0).getAsDouble();
                double lat = coordPair.get(1).getAsDouble();
                jsArrayBuilder.append(",[").append(lat).append(",").append(lon).append("]");
            }
            if (coordinates.size() > 1) {
                jsArrayBuilder.append(",[").append(endLat).append(",").append(endLon).append("]");
            }
            jsArrayBuilder.append("]");
            String jsArrayString = jsArrayBuilder.toString();
            String jsCall = String.format("drawRoute(%s, %d)", jsArrayString, computedDurationHours);
            Platform.runLater(() -> webEngine.executeScript(jsCall));
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "An error occurred while searching for the route.");
        }
    }




    public static class expedition {
        String name;
        List<Waypoint> waypoints = new ArrayList<>();

        public expedition(String name) {
            this.name = name;
        }

        public void addWaypoint(double lat, double lng, String name) {
            waypoints.add(new Waypoint(lat, lng, name));
        }

        public static class Waypoint {
            double lat;
            double lng;
            String name;

            public Waypoint(double lat, double lng, String name) {
                this.lat = lat;
                this.lng = lng;
                this.name = name;
            }
        }
    }

    private static class ExpeditionUpdate {
        String text;
        String imageUrl;
        String location;
        String timestamp;
        ExpeditionUpdate(String text, String imageUrl, String location, String timestamp) {
            this.text = text;
            this.imageUrl = imageUrl;
            this.location = location;
            this.timestamp = timestamp;
        }
    }

    private static class Refuge {
        String name;
        double lat;
        double lon;
        Refuge(String name, double lat, double lon) {
            this.name = name;
            this.lat = lat;
            this.lon = lon;
        }
    }

    private void initializeDummyExpeditions() {
        expedition expedition1 = new expedition("Alps Adventure");
        expedition1.addWaypoint(46.558, 10.295, "Start");
        expedition1.addWaypoint(46.565, 10.300, "Midpoint");
        expedition1.addWaypoint(46.573, 10.315, "Summit");
    }
}
