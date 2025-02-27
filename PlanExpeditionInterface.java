package me.ancastanoev.interfaces;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.ancastanoev.Climber;
import me.ancastanoev.database.GuideDAO;
import me.ancastanoev.imageapi.ImageService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlanExpeditionInterface {

    private static final String GRAPH_HOPPER_API_KEY = "9558b9c0-cf77-44a1-947a-20b16fbc1e76";
    private static final List<PredefinedExpedition> predefinedExpeditions = getPredefinedExpeditions();

    public static void show(Stage stage, Scene userScene, String currentRole) {
        if (!"user".equalsIgnoreCase(currentRole)) {
            showMessage("Access Denied", "Only users can plan expeditions.");
            return;
        }
        Climber loggedInClimber = ClimberAppInterface.getLoggedInUser();
        if (loggedInClimber == null) {
            showMessage("Error", "You must be logged in to plan an expedition.");
            return;
        }
        BorderPane root = new BorderPane();
        Button topBackButton = new Button("Back to Menu");
        topBackButton.setOnAction(e -> stage.setScene(userScene));
        HBox topBar = new HBox(topBackButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));
        root.setTop(topBar);
        WebView mapView = new WebView();
        mapView.setPrefSize(800, 600);
        WebEngine webEngine = mapView.getEngine();
        URL mapUrl = PlanExpeditionInterface.class.getResource("/org/example/demo1/map.html");
        if (mapUrl != null) {
            webEngine.load(mapUrl.toExternalForm());
        } else {
            System.err.println("Error: map.html not found!");
        }
        root.setCenter(mapView);
        VBox rightPane = new VBox(15);
        rightPane.setPadding(new Insets(10));
        rightPane.setPrefWidth(350);
        Label planningLabel = new Label("Plan an Expedition");
        planningLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label expeditionOptionLabel = new Label("Select Expedition Option:");
        ComboBox<PredefinedExpedition> expeditionOptionBox = new ComboBox<>();
        expeditionOptionBox.setPrefWidth(300);
        for (PredefinedExpedition exp : predefinedExpeditions) {
            if (isExpeditionAllowed(loggedInClimber.getExperienceLevel(), exp.difficulty)) {
                expeditionOptionBox.getItems().add(exp);
            }
        }
        CheckBox customExpeditionCheckBox = new CheckBox("Custom Expedition");
        TextField customStartTextField = new TextField();
        customStartTextField.setPromptText("Custom Start Location");
        customStartTextField.setDisable(true);
        TextField customEndTextField = new TextField();
        customEndTextField.setPromptText("Custom End Location");
        customEndTextField.setDisable(true);
        customExpeditionCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            customStartTextField.setDisable(!newVal);
            customEndTextField.setDisable(!newVal);
            expeditionOptionBox.setDisable(newVal);
        });
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        Button getWeatherButton = new Button("Get Weather Forecast");
        Button planButton = new Button("Plan Expedition");
        TextArea expeditionDetailsArea = new TextArea();
        expeditionDetailsArea.setEditable(false);
        expeditionDetailsArea.setPrefHeight(100);
        expeditionDetailsArea.setWrapText(true);
        ImageView guideImageView = new ImageView();
        guideImageView.setFitWidth(150);
        guideImageView.setFitHeight(150);
        guideImageView.setPreserveRatio(true);
        guideImageView.setVisible(false);
        Label reviewsLabel = new Label("Reviews:");
        reviewsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        VBox reviewsPane = new VBox(5);
        reviewsPane.setPadding(new Insets(5));
        reviewsPane.setStyle("-fx-background-color: #F0F8FF; -fx-border-color: #B0C4DE; -fx-border-radius: 5; -fx-background-radius: 5;");
        Label galleriesLabel = new Label("Image Galleries:");
        galleriesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        VBox startGalleryPane = new VBox(5);
        startGalleryPane.setPadding(new Insets(5));
        startGalleryPane.setStyle("-fx-background-color: #FFFACD; -fx-border-color: #DAA520; -fx-border-radius: 5; -fx-background-radius: 5;");
        VBox destinationGalleryPane = new VBox(5);
        destinationGalleryPane.setPadding(new Insets(5));
        destinationGalleryPane.setStyle("-fx-background-color: #FFFACD; -fx-border-color: #DAA520; -fx-border-radius: 5; -fx-background-radius: 5;");
        VBox planningSection = new VBox(10, planningLabel, expeditionOptionLabel, expeditionOptionBox, customExpeditionCheckBox, customStartTextField, customEndTextField, startDatePicker, endDatePicker, getWeatherButton, planButton, expeditionDetailsArea, guideImageView, reviewsLabel, reviewsPane, galleriesLabel, startGalleryPane, destinationGalleryPane);
        planningSection.setPadding(new Insets(10));
        planningSection.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, Insets.EMPTY)));
        Button rightBackButton = new Button("Back to Menu");
        rightBackButton.setOnAction(e -> stage.setScene(userScene));
        rightPane.getChildren().addAll(planningSection, rightBackButton);
        ScrollPane rightScrollPane = new ScrollPane();
        rightScrollPane.setContent(rightPane);
        rightScrollPane.setFitToWidth(true);
        rightScrollPane.setFitToHeight(true);
        root.setRight(rightScrollPane);
        VBox weatherPane = new VBox(5);
        weatherPane.setPadding(new Insets(10));
        Label weatherLabel = new Label("Weather Forecast: N/A");
        weatherLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        weatherPane.getChildren().addAll(weatherLabel);
        root.setBottom(weatherPane);
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        planButton.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            if (startDate == null || endDate == null) {
                showMessage("Error", "Please choose both start and end dates!");
                return;
            }
            if (!startDate.isBefore(endDate)) {
                showMessage("Error", "Start date must be before the end date.");
                return;
            }
            if (customExpeditionCheckBox.isSelected()) {
                String customStart = customStartTextField.getText().trim();
                String customEnd = customEndTextField.getText().trim();
                if (customStart.isEmpty() || customEnd.isEmpty()) {
                    showMessage("Error", "Please enter both custom start and end locations!");
                    return;
                }
                String details = "Expedition: Custom Expedition\nStart Location: " + customStart + "\nEnd Location: " + customEnd + "\nDates: " + startDate.toString() + " to " + endDate.toString();
                expeditionDetailsArea.setText(details);
                guideImageView.setImage(new Image("https://via.placeholder.com/150?text=Guide"));
                guideImageView.setVisible(true);
                reviewsPane.getChildren().clear();
                startGalleryPane.getChildren().clear();
                destinationGalleryPane.getChildren().clear();
                displayImageGalleryForLocation(customStart, "Start: " + customStart, startGalleryPane);
                displayImageGalleryForLocation(customEnd, "Destination: " + customEnd, destinationGalleryPane);
                searchAndDisplayRoute(customStart, customEnd, 0, webEngine);
            } else {
                PredefinedExpedition selectedExpedition = expeditionOptionBox.getValue();
                if (selectedExpedition == null) {
                    showMessage("Error", "Please select an expedition option!");
                    return;
                }
                String guideName = GuideDAO.getGuideForLevel(loggedInClimber.getExperienceLevel(), selectedExpedition.difficulty);
                if (guideName == null || guideName.isEmpty()) {
                    guideName = selectedExpedition.guide;
                }
                guideImageView.setVisible(false);
                String starRating = getStarRatingString(selectedExpedition.rating);
                String details = "Expedition: " + selectedExpedition.name + "\nGuide: " + guideName + "\nOverall Rating: " + starRating + "\nDates: " + startDate.toString() + " to " + endDate.toString();
                expeditionDetailsArea.setText(details);
                reviewsPane.getChildren().clear();
                if (selectedExpedition.reviews != null) {
                    for (Review rev : selectedExpedition.reviews) {
                        Label reviewerLabel = new Label(rev.reviewer + ": ");
                        reviewerLabel.setStyle("-fx-font-weight: bold;");
                        Label reviewText = new Label(rev.reviewText);
                        Label reviewStars = new Label(getStarRatingString(rev.stars));
                        HBox reviewBubble = new HBox(5, reviewerLabel, reviewText, reviewStars);
                        reviewBubble.setPadding(new Insets(3));
                        reviewBubble.setStyle("-fx-background-color: #E0F7FA; -fx-border-color: #B2EBF2; -fx-border-radius: 3; -fx-background-radius: 3;");
                        reviewsPane.getChildren().add(reviewBubble);
                    }
                }
                startGalleryPane.getChildren().clear();
                destinationGalleryPane.getChildren().clear();
                displayImageGalleryForLocation(selectedExpedition.startLocation, "Start: " + selectedExpedition.startLocation, startGalleryPane);
                displayImageGalleryForLocation(selectedExpedition.endLocation, "Destination: " + selectedExpedition.endLocation, destinationGalleryPane);
                searchAndDisplayRoute(selectedExpedition.startLocation, selectedExpedition.endLocation, 0, webEngine);
            }
        });
        getWeatherButton.setOnAction(e -> {
            String startLoc, endLoc;
            if (customExpeditionCheckBox.isSelected()) {
                startLoc = customStartTextField.getText().trim();
                endLoc = customEndTextField.getText().trim();
            } else {
                PredefinedExpedition selectedExpedition = expeditionOptionBox.getValue();
                if (selectedExpedition == null) {
                    showMessage("Error", "Please select an expedition option for the weather forecast.");
                    return;
                }
                startLoc = selectedExpedition.startLocation;
                endLoc = selectedExpedition.endLocation;
            }
            LocalDate start = startDatePicker.getValue();
            if (startLoc.isEmpty() || endLoc.isEmpty() || start == null) {
                showMessage("Error", "Please provide both start and end locations and choose a Start Date for the weather forecast.");
                return;
            }
            CompletableFuture<String> startForecastFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return getWeatherForecastSync(startLoc, start.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "Error fetching forecast for start location.";
                }
            });
            CompletableFuture<String> endForecastFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return getWeatherForecastSync(endLoc, start.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "Error fetching forecast for destination.";
                }
            });
            CompletableFuture.allOf(startForecastFuture, endForecastFuture).thenRun(() -> {
                String startForecast = startForecastFuture.join();
                String endForecast = endForecastFuture.join();
                String combined = "Weather Forecast for Start (" + startLoc + "):\n" + startForecast + "\n\nWeather Forecast for Destination (" + endLoc + "):\n" + endForecast;
                Platform.runLater(() -> weatherLabel.setText(combined));
            });
        });
    }

    private static boolean isExpeditionAllowed(String userExp, String expeditionDifficulty) {
        userExp = userExp.toLowerCase();
        expeditionDifficulty = expeditionDifficulty.toLowerCase();
        if (userExp.equals("beginner")) {
            return expeditionDifficulty.equals("easy");
        } else if (userExp.equals("intermediate")) {
            return expeditionDifficulty.equals("easy") || expeditionDifficulty.equals("moderate");
        } else if (userExp.equals("expert")) {
            return expeditionDifficulty.equals("easy") || expeditionDifficulty.equals("moderate") || expeditionDifficulty.equals("difficult");
        }
        return false;
    }

    private static String getStarRatingString(double rating) {
        int fullStars = (int) Math.floor(rating);
        int emptyStars = 5 - fullStars;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fullStars; i++) {
            sb.append("★");
        }
        for (int i = 0; i < emptyStars; i++) {
            sb.append("☆");
        }
        return sb.toString();
    }

    private static void displayImageGalleryForLocation(String query, String title, VBox container) {
        new Thread(() -> {
            ImageService imageService = new ImageService();
            try {
                List<String> imageUrls = imageService.getImageUrls(query);
                int maxImages = Math.min(5, imageUrls.size());
                if (maxImages == 0) return;
                HBox hbox = new HBox(10);
                hbox.setPadding(new Insets(5));
                for (int i = 0; i < maxImages; i++) {
                    String url = imageUrls.get(i);
                    ImageView iv = new ImageView(new Image(url, 100, 0, true, true));
                    iv.setOnMouseClicked(ev -> System.out.println("Selected image: " + url));
                    hbox.getChildren().add(iv);
                }
                VBox galleryBox = new VBox(5);
                galleryBox.getChildren().addAll(new Label(title), hbox);
                Platform.runLater(() -> container.getChildren().add(galleryBox));
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    static void updateLiveImage(ImageView imageView, String query) {
        new Thread(() -> {
            ImageService imageService = new ImageService();
            try {
                List<String> imageUrls = imageService.getImageUrls(query);
                if (!imageUrls.isEmpty()) {
                    String imageUrl = imageUrls.get(0);
                    Platform.runLater(() -> {
                        System.out.println("Updating live image: " + imageUrl);
                        imageView.setImage(new Image(imageUrl, 280, 200, true, true));
                    });
                } else {
                    System.out.println("No images found for: " + query);
                }
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private static void searchAndDisplayRoute(String startPlace, String endPlace, long unusedDuration, WebEngine webEngine) {
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
                showMessage("Error", "Start location not found.");
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
                showMessage("Error", "End location not found.");
                return;
            }
            JsonObject endObj = endResults.get(0).getAsJsonObject();
            double endLat = Double.parseDouble(endObj.get("lat").getAsString());
            double endLon = Double.parseDouble(endObj.get("lon").getAsString());
            String graphHopperUrl = String.format(
                    "https://graphhopper.com/api/1/route?point=%f,%f&point=%f,%f&vehicle=foot&locale=en&points_encoded=false&key=%s",
                    startLat, startLon, endLat, endLon, GRAPH_HOPPER_API_KEY);
            HttpRequest ghRequest = HttpRequest.newBuilder().uri(URI.create(graphHopperUrl)).build();
            HttpResponse<String> ghResponse = client.send(ghRequest, HttpResponse.BodyHandlers.ofString());
            JsonObject ghJson = new Gson().fromJson(ghResponse.body(), JsonObject.class);
            JsonArray paths = ghJson.getAsJsonArray("paths");
            if (paths.size() == 0) {
                showMessage("Error", "No route found.");
                return;
            }
            JsonObject pathObj = paths.get(0).getAsJsonObject();
            double distanceMeters = pathObj.get("distance").getAsDouble();
            double computedDistanceKm = distanceMeters / 1000.0;
            double durationHoursDecimal = computedDistanceKm / 3;
            int hours = (int) durationHoursDecimal;
            int minutes = (int) Math.round((durationHoursDecimal - hours) * 60);
            String formattedDuration = hours + "h" + (minutes > 0 ? minutes + "min" : "");
            JsonObject pointsObj = pathObj.getAsJsonObject("points");
            JsonArray coordinates = pointsObj.getAsJsonArray("coordinates");
            StringBuilder jsArrayBuilder = new StringBuilder("[");
            for (int i = 0; i < coordinates.size(); i++) {
                JsonArray coordPair = coordinates.get(i).getAsJsonArray();
                double lon = coordPair.get(0).getAsDouble();
                double lat = coordPair.get(1).getAsDouble();
                if (i > 0) {
                    jsArrayBuilder.append(",");
                }
                jsArrayBuilder.append("[").append(lat).append(",").append(lon).append("]");
            }
            jsArrayBuilder.append("]");
            String jsArrayString = jsArrayBuilder.toString();
            String routeInfo = String.format("%s, %.1f km", formattedDuration, computedDistanceKm);
            String jsCall = String.format("drawRoute(%s, '%s')", jsArrayString, routeInfo);
            Platform.runLater(() -> webEngine.executeScript(jsCall));
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error", "An error occurred while searching for the route.");
        }
    }

    private static String getWeatherForecastSync(String location, String date) throws Exception {
        String geocodeUrl = "https://nominatim.openstreetmap.org/search?format=json&q=" + URLEncoder.encode(location, StandardCharsets.UTF_8);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest geocodeRequest = HttpRequest.newBuilder().uri(URI.create(geocodeUrl)).header("User-Agent", "JavaFXApp").build();
        HttpResponse<String> geocodeResponse = client.send(geocodeRequest, HttpResponse.BodyHandlers.ofString());
        JsonArray geoResults = new Gson().fromJson(geocodeResponse.body(), JsonArray.class);
        if (geoResults.size() == 0) {
            return "Location not found.";
        }
        JsonObject firstResult = geoResults.get(0).getAsJsonObject();
        double lat = Double.parseDouble(firstResult.get("lat").getAsString());
        double lon = Double.parseDouble(firstResult.get("lon").getAsString());
        String apiKey = "61844573fbe9db29062b0dfc11f1fac1";
        String weatherUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";
        weatherUrl = weatherUrl.replace("\n", "").replace("\r", "");
        HttpRequest weatherRequest = HttpRequest.newBuilder().uri(URI.create(weatherUrl)).build();
        HttpResponse<String> weatherResponse = client.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
        JsonObject json = new Gson().fromJson(weatherResponse.body(), JsonObject.class);
        if (!json.has("list") || json.get("list").isJsonNull()) {
            return "No forecast available.";
        }
        JsonArray list = json.getAsJsonArray("list");
        double windSpeed = 0.0;
        String weatherDescription = null;
        double temperature = 0.0;
        for (int i = 0; i < list.size(); i++) {
            JsonObject entry = list.get(i).getAsJsonObject();
            String dt_txt = entry.get("dt_txt").getAsString();
            if (dt_txt.startsWith(date)) {
                JsonObject main = entry.getAsJsonObject("main");
                temperature = main.get("temp").getAsDouble();
                JsonArray weatherArr = entry.getAsJsonArray("weather");
                weatherDescription = weatherArr.get(0).getAsJsonObject().get("description").getAsString();
                JsonObject windObj = entry.getAsJsonObject("wind");
                windSpeed = windObj.get("speed").getAsDouble();
                break;
            }
        }
        if (weatherDescription == null && list.size() > 0) {
            JsonObject entry = list.get(0).getAsJsonObject();
            JsonObject main = entry.getAsJsonObject("main");
            temperature = main.get("temp").getAsDouble();
            JsonArray weatherArr = entry.getAsJsonArray("weather");
            weatherDescription = weatherArr.get(0).getAsJsonObject().get("description").getAsString();
            JsonObject windObj = entry.getAsJsonObject("wind");
            windSpeed = windObj.get("speed").getAsDouble();
        }
        String summary = "Wind Speed: " + windSpeed + " m/s" + "\nWeather: " + weatherDescription + "\nTemperature: " + temperature + "°C";
        return summary;
    }

    public static class PredefinedExpedition {
        String name;
        String startLocation;
        String endLocation;
        String difficulty;
        String guide;
        double rating;
        List<Review> reviews;

        public PredefinedExpedition(String name, String startLocation, String endLocation, String difficulty, String guide, double rating, List<Review> reviews) {
            this.name = name;
            this.startLocation = startLocation;
            this.endLocation = endLocation;
            this.difficulty = difficulty;
            this.guide = guide;
            this.rating = rating;
            this.reviews = reviews;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Review {
        String reviewer;
        String reviewText;
        double stars;

        public Review(String reviewer, String reviewText, double stars) {
            this.reviewer = reviewer;
            this.reviewText = reviewText;
            this.stars = stars;
        }
    }

    private static List<PredefinedExpedition> getPredefinedExpeditions() {
        List<PredefinedExpedition> list = new ArrayList<>();
        List<Review> reviews1 = new ArrayList<>();
        reviews1.add(new Review("Anna Wintour", "Great hike, suited for children.", 4.0));
        reviews1.add(new Review("John Doe", "Stunning views but challenging.", 3.5));
        reviews1.add(new Review("Jane Smith", "An unforgettable experience.", 4.5));
        list.add(new PredefinedExpedition("Moldoveanu Peak Expedition", "Stana lui Burnei", "Moldoveanu Peak", "moderate", "Guide Ion", 4.0, reviews1));
        List<Review> reviews2 = new ArrayList<>();
        reviews2.add(new Review("Alice", "A tough climb, but rewarding.", 4.8));
        reviews2.add(new Review("Bob", "Exhilarating adventure!", 4.5));
        reviews2.add(new Review("Charlie", "Not for the faint-hearted.", 4.2));
        list.add(new PredefinedExpedition("Munții Țarcu Hike", "Muntele Mic", "Țarcu", "difficult", "Guide Maria", 4.8, reviews2));
        List<Review> reviews3 = new ArrayList<>();
        reviews3.add(new Review("Dave", "Perfect for a relaxed day out.", 3.5));
        reviews3.add(new Review("Eve", "Lovely scenery and easy trail.", 3.5));
        reviews3.add(new Review("Frank", "A great walk through nature.", 3.5));
        list.add(new PredefinedExpedition("Piatra Craiului Tour", "Zărnești", "Piatra Craiului", "easy", "Guide Andrei", 3.5, reviews3));
        List<Review> reviews4 = new ArrayList<>();
        reviews4.add(new Review("Grace", "The vistas were breathtaking.", 4.2));
        reviews4.add(new Review("Heidi", "Moderate difficulty with rewarding views.", 4.0));
        reviews4.add(new Review("Ivan", "A nice tour of the alpine region.", 4.2));
        list.add(new PredefinedExpedition("Alpine Vista Tour", "Refuge du Plan de l'Aiguille", "Aiguille du Midi", "moderate", "Guide Stefan", 4.2, reviews4));
        return list;
    }

    private static boolean isExperienceLevelMatching(String climberExperienceLevel, String routeDifficulty) {
        switch (climberExperienceLevel.toLowerCase()) {
            case "beginner":
                return "easy".equalsIgnoreCase(routeDifficulty);
            case "intermediate":
                return "easy".equalsIgnoreCase(routeDifficulty) || "moderate".equalsIgnoreCase(routeDifficulty);
            case "expert":
                return "easy".equalsIgnoreCase(routeDifficulty) || "moderate".equalsIgnoreCase(routeDifficulty) || "difficult".equalsIgnoreCase(routeDifficulty);
            default:
                return false;
        }
    }

    private static boolean validateDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    private static boolean isStartDateBeforeEndDate(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return start.isBefore(end);
    }

    private static void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String escapeJS(String input) {
        return input.replace("'", "\\'");
    }
}
