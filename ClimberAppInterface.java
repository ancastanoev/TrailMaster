package me.ancastanoev.interfaces;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.ancastanoev.Climber;
import me.ancastanoev.client.EmergencyAdminInterface;
import me.ancastanoev.client.ExpeditionChatClientFX;
import me.ancastanoev.client.ExpeditionReportingInterface;
import me.ancastanoev.client.GearClientScene;
import me.ancastanoev.client.ClimberProfileInterface;
import me.ancastanoev.database.RouteDAO;
import me.ancastanoev.imageapi.ImageService;
import me.ancastanoev.Route;
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

public class ClimberAppInterface extends Application {

    private String currentRole = "";
    private static List<expedition> plannedExpeditions = new ArrayList<>();
    private Timeline simulationTimeline;
    private static final int TOTAL_SIMULATION_TICKS = 10;
    private static String simulationStartQuery = "Alps Base Camp";
    private static String simulationDestQuery = "Alpine Summit";
    private String currentLocation = null;

    @Override
    public void start(Stage stage) {
        currentLocation = fetchLiveLocation();
        initializeDummyExpeditions();

        VBox mainMenu = new VBox(15);
        mainMenu.setPadding(new Insets(20));
        mainMenu.setAlignment(Pos.CENTER);
        Image bgImage = new Image("file:src/main/java/me/ancastanoev/image.jpg.jpg", true);
        if (bgImage.isError()) {
            System.out.println("Error loading image: " + bgImage.getException());
        }
        BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage bgImageObj = new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize);
        mainMenu.setBackground(new Background(bgImageObj));

        Button userButton = createStyledButton("User Mode", Color.LIGHTBLUE);
        Button adminButton = createStyledButton("Admin Mode", Color.LIGHTGREEN);
        Button emergencyAdminButton = createStyledButton("Emergency Admin", Color.ORANGE);
        Button helpButton = createStyledButton("Help", Color.LIGHTCORAL);

        mainMenu.getChildren().addAll(userButton, adminButton, emergencyAdminButton, helpButton);
        Scene mainScene = new Scene(mainMenu, 1100, 700);
        stage.setTitle("Climber Application");
        stage.setScene(mainScene);
        stage.setFullScreen(true);
        stage.show();

        VBox userMenu = new VBox(15);
        userMenu.setPadding(new Insets(20));
        userMenu.setAlignment(Pos.CENTER);
        userMenu.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, Insets.EMPTY)));

        Button loginButton = createStyledButton("Log In", Color.LIGHTSTEELBLUE);
        Button createAccountButton = createStyledButton("Create Account", Color.DARKCYAN);
        Button planExpeditionButton = createStyledButton("Plan Expedition", Color.DARKSLATEBLUE);
        Button gearButton = createStyledButton("Gear Client", Color.MEDIUMPURPLE);
        Button viewExpeditionsButton = createStyledButton("View Planned Expeditions", Color.DARKSLATEGRAY);
        Button expeditionChatButton = createStyledButton("Expedition Group Chat", Color.DARKMAGENTA);
        Button profileButton = createStyledButton("My Profile", Color.DARKCYAN);
        Button reportUpdateButton = createStyledButton("Report Update", Color.MIDNIGHTBLUE);
        Button backToMainButton = createStyledButton("Back to Main Menu", Color.CADETBLUE);

        userMenu.getChildren().addAll(loginButton, createAccountButton, planExpeditionButton,
                gearButton, viewExpeditionsButton, expeditionChatButton, profileButton, reportUpdateButton, backToMainButton);
        Scene userScene = new Scene(userMenu, 400, 300);

        VBox adminMenu = new VBox(15);
        adminMenu.setPadding(new Insets(20));
        adminMenu.setAlignment(Pos.CENTER);
        adminMenu.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, Insets.EMPTY)));

        Button createClimberButton = createStyledButton("Create Climber Profile", Color.DARKMAGENTA);
        Button addMountainButton = createStyledButton("Add Mountain", Color.DARKSLATEGRAY);
        Button addRouteButton = createStyledButton("Add Route", Color.DARKCYAN);
        Button addGuideButton = createStyledButton("Add Guide", Color.DARKORANGE);
        Button sortClimbersButton = createStyledButton("Sort Climbers", Color.DARKBLUE);
        Button displayClimbersButton = createStyledButton("Display Climbers by Experience", Color.DARKVIOLET);
        Button changeExperienceButton = createStyledButton("Change Experience Level", Color.DARKOLIVEGREEN);
        Button backToMainAdminButton = createStyledButton("Back to Main Menu", Color.DARKRED);

        adminMenu.getChildren().addAll(createClimberButton, addMountainButton, addRouteButton, addGuideButton,
                sortClimbersButton, displayClimbersButton, changeExperienceButton, backToMainAdminButton);
        Scene adminScene = new Scene(adminMenu, 500, 400);

        userButton.setOnAction(e -> {
            currentRole = "user";
            switchScene(stage, userScene);
        });
        adminButton.setOnAction(e -> {
            currentRole = "admin";
            switchScene(stage, adminScene);
        });
        emergencyAdminButton.setOnAction(e -> {
            EmergencyAdminInterface.showEmergencyAdmin(stage, mainScene);
            stage.setFullScreen(true);
        });
        helpButton.setOnAction(e -> {
            HelpInterface.show(stage, mainScene);
            stage.setFullScreen(true);
        });
        loginButton.setOnAction(e -> {
            UserLoginInterface.show(stage, userScene);
            stage.setFullScreen(true);
        });
        createAccountButton.setOnAction(e -> {
            UserLoginInterface.show(stage, userScene);
            stage.setFullScreen(true);
        });
        planExpeditionButton.setOnAction(e -> {
            PlanExpeditionInterface.show(stage, userScene, currentRole);
            stage.setFullScreen(true);
        });
        gearButton.setOnAction(e -> {
            Scene gearScene = GearClientScene.createScene(stage, userScene);
            switchScene(stage, gearScene);
        });
        viewExpeditionsButton.setOnAction(e -> showExpeditionsList(stage, userScene));
        expeditionChatButton.setOnAction(e -> {
            if (me.ancastanoev.Application.getLoggedInUser() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You must be logged in to access the expedition chat.");
                alert.showAndWait();
                return;
            }
            String expeditionId = "Expedition1";
            ExpeditionChatClientFX.showChat(expeditionId);
        });
        profileButton.setOnAction(e -> {
            if (me.ancastanoev.Application.getLoggedInUser() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You must be logged in to view your profile.");
                alert.showAndWait();
                return;
            }
            ClimberProfileInterface.showProfile(stage, userScene);
            stage.setFullScreen(true);
        });
        reportUpdateButton.setOnAction(e -> {
            ExpeditionReportingInterface reportingInterface = new ExpeditionReportingInterface();
            Stage reportStage = new Stage();
            try {
                reportingInterface.start(reportStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        backToMainButton.setOnAction(e -> {
            currentRole = "";
            switchScene(stage, mainScene);
        });
        createClimberButton.setOnAction(e -> {
            CreateClimberInterface.show(stage, adminScene, currentRole);
            stage.setFullScreen(true);
        });
        addMountainButton.setOnAction(e -> {
            AddMountainInterface.show(stage, adminScene, currentRole);
            stage.setFullScreen(true);
        });
        addRouteButton.setOnAction(e -> {
            AddRouteInterface.show(stage, adminScene, currentRole);
            stage.setFullScreen(true);
        });
        addGuideButton.setOnAction(e -> {
            AddGuideInterface.show(stage, adminScene, currentRole);
            stage.setFullScreen(true);
        });
        sortClimbersButton.setOnAction(e -> {
            SortClimbersInterface.show(stage, adminScene, currentRole);
            stage.setFullScreen(true);
        });
        displayClimbersButton.setOnAction(e -> {
            DisplayClimbersByExperienceInterface.show(stage, adminScene, currentRole);
            stage.setFullScreen(true);
        });
        changeExperienceButton.setOnAction(e -> {
            ChangeExperienceInterface.show(stage, adminScene, currentRole);
            stage.setFullScreen(true);
        });
        backToMainAdminButton.setOnAction(e -> {
            currentRole = "";
            switchScene(stage, mainScene);
        });
    }

    private void switchScene(Stage stage, Scene scene) {
        stage.setScene(scene);
        stage.setFullScreen(true);
    }

    private void initializeDummyExpeditions() {
        expedition expedition1 = new expedition("Alps Adventure");
        expedition1.addWaypoint(46.558, 10.295, "Start");
        expedition1.addWaypoint(46.565, 10.300, "Midpoint");
        expedition1.addWaypoint(46.573, 10.315, "Summit");
        plannedExpeditions.add(expedition1);

        expedition expedition2 = new expedition("Mountain Quest");
        expedition2.addWaypoint(44.4268, 26.1025, "Start");
        expedition2.addWaypoint(44.4300, 26.1080, "Midway");
        expedition2.addWaypoint(44.4350, 26.1150, "Summit");
        plannedExpeditions.add(expedition2);
    }

    private Button createStyledButton(String text, Color backgroundColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setTextFill(Color.WHITE);
        button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(5), null)));
        button.setStyle("-fx-cursor: hand; -fx-padding: 12 20;");
        button.setOnMouseEntered(e -> button.setOpacity(0.8));
        button.setOnMouseExited(e -> button.setOpacity(1.0));
        return button;
    }

    public static List<expedition> getPlannedExpeditions() {
        return plannedExpeditions;
    }

    private void showExpeditionsList(Stage stage, Scene previousScene) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #F0F8FF; -fx-border-color: #B0C4DE; -fx-border-radius: 10; -fx-background-radius: 10;");
        Label titleLabel = new Label("Your Planned Expedition");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        expedition firstExpedition = plannedExpeditions.get(0);
        Label expeditionName = new Label(firstExpedition.name);
        expeditionName.setFont(Font.font("Arial", FontWeight.NORMAL, 22));
        Button simulateButton = createStyledButton("Simulate Expedition", Color.DARKGREEN);
        Button backButton = createStyledButton("Back", Color.DARKRED);
        root.getChildren().addAll(titleLabel, expeditionName, simulateButton, backButton);
        Scene expeditionScene = new Scene(root, 400, 400);
        switchScene(stage, expeditionScene);
        simulateButton.setOnAction(e -> showExpeditionMapSimulation(stage, firstExpedition, expeditionScene));
        backButton.setOnAction(e -> switchScene(stage, previousScene));
    }

    private void showExpeditionMapSimulation(Stage stage, expedition expedition, Scene previousScene) {
        BorderPane root = new BorderPane();
        WebView mapView = new WebView();
        mapView.setPrefSize(1100, 800);
        mapView.setMinSize(1100, 800);
        mapView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        WebEngine webEngine = mapView.getEngine();
        URL mapUrl = getClass().getResource("/org/example/demo1/map.html");
        if (mapUrl != null) {
            webEngine.load(mapUrl.toExternalForm());
        } else {
            System.err.println("Error: map.html not found!");
        }
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                Platform.runLater(() -> {
                    webEngine.executeScript("refreshMap()");
                    webEngine.executeScript("setHikerIcon()");
                });
            }
        });
        VBox sidePane = new VBox(10);
        sidePane.setPadding(new Insets(10));
        sidePane.setPrefWidth(300);
        Label expLabel = new Label("Expedition: " + expedition.name);
        expLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        ImageView liveImageView = new ImageView(new Image("https://via.placeholder.com/280x200.png?text=Live+Image"));
        liveImageView.setFitWidth(280);
        liveImageView.setFitHeight(200);
        liveImageView.setPreserveRatio(true);
        Label simStatusLabel = new Label("Status: Waiting to start simulation...");
        ProgressBar simProgressBar = new ProgressBar(0);
        Button startSimButton = createStyledButton("Start Simulation", Color.DARKGREEN);
        Button stopSimButton = createStyledButton("Stop Simulation", Color.DARKORANGE);
        Button backButton = createStyledButton("Back", Color.DARKRED);
        sidePane.getChildren().addAll(expLabel, liveImageView, simStatusLabel, simProgressBar, startSimButton, stopSimButton, backButton);
        root.setCenter(mapView);
        root.setRight(sidePane);
        Scene simulationScene = new Scene(root, 1100, 700);
        switchScene(stage, simulationScene);
        webEngine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
            if (newDoc != null) {
                for (expedition.Waypoint wp : expedition.waypoints) {
                    String script = String.format("addMarker(%f, %f, '%s')", wp.lat, wp.lng, wp.name);
                    webEngine.executeScript(script);
                }
                if (!expedition.waypoints.isEmpty()) {
                    expedition.Waypoint first = expedition.waypoints.get(0);
                    String initScript = String.format("updateMarker(%f, %f)", first.lat, first.lng);
                    webEngine.executeScript(initScript);
                }
                Platform.runLater(() -> webEngine.executeScript("refreshMap()"));
            }
        });
        final int[] tickCount = {0};
        final String[] simulationMessages = {
                "At Alps Base Camp",
                "Leaving Base Camp",
                "Ascending along the trail",
                "Scenic overlook ahead",
                "Halfway there",
                "Look! A bear spotted!",
                "Approaching Alpine Summit",
                "Almost at the Summit",
                "Summit reached!",
                "Expedition complete!"
        };
        java.util.function.Function<Integer, String> getLiveImageQueryForTick = (tick) -> {
            switch (tick) {
                case 0: return "Alpine valley";
                case 1: return "Alps valley departure";
                case 2: return "Alpine trail";
                case 3: return "Scenic mountain view";
                case 4: return "Mountain midpoint";
                case 5: return "bear ";
                case 6: return "Alpine Summit ";
                case 7: return "Mont Blanc";
                case 8: return "Alpine Summit view";
                case 9: return "Hikers";
                default: return expedition.waypoints.get(expedition.waypoints.size() - 1).name;
            }
        };
        startSimButton.setOnAction(e -> {
            if (simulationTimeline != null) {
                simulationTimeline.stop();
            }
            int numWaypoints = expedition.waypoints.size();
            if (numWaypoints < 2) {
                showMessage("Warning", "Not enough waypoints for simulation.");
                return;
            }
            updateLiveImageWithTick(liveImageView, getLiveImageQueryForTick.apply(0), 0);
            simStatusLabel.setText(simulationMessages[0]);
            simProgressBar.setProgress(0);
            tickCount[0] = 0;
            simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                double progress = (double) tickCount[0] / (TOTAL_SIMULATION_TICKS - 1);
                simProgressBar.setProgress(progress);
                double globalFraction = progress;
                int segmentIndex = (int) (globalFraction * (numWaypoints - 1));
                if (segmentIndex >= numWaypoints - 1) {
                    segmentIndex = numWaypoints - 2;
                }
                double segmentFraction = (globalFraction * (numWaypoints - 1)) - segmentIndex;
                expedition.Waypoint startWp = expedition.waypoints.get(segmentIndex);
                expedition.Waypoint endWp = expedition.waypoints.get(segmentIndex + 1);
                double currentLat = startWp.lat + segmentFraction * (endWp.lat - startWp.lat);
                double currentLng = startWp.lng + segmentFraction * (endWp.lng - startWp.lng);
                String script = String.format("updateMarker(%f, %f)", currentLat, currentLng);
                Platform.runLater(() -> {
                    webEngine.executeScript(script);
                    webEngine.executeScript("setHikerIcon()");
                });
                String liveQuery = getLiveImageQueryForTick.apply(tickCount[0]);
                updateLiveImageWithTick(liveImageView, liveQuery, tickCount[0]);
                int msgIndex = Math.min(tickCount[0], simulationMessages.length - 1);
                simStatusLabel.setText(simulationMessages[msgIndex]);
                tickCount[0]++;
                if (tickCount[0] >= TOTAL_SIMULATION_TICKS) {
                    simulationTimeline.stop();
                }
            }));
            simulationTimeline.setCycleCount(TOTAL_SIMULATION_TICKS);
            simulationTimeline.play();
        });
        stopSimButton.setOnAction(e -> {
            if (simulationTimeline != null) {
                simulationTimeline.stop();
            }
        });
        backButton.setOnAction(e -> {
            if (simulationTimeline != null) {
                simulationTimeline.stop();
            }
            switchScene(stage, previousScene);
        });
    }

    static void updateLiveImageWithTick(ImageView imageView, String query, int tick) {
        new Thread(() -> {
            ImageService imageService = new ImageService();
            try {
                List<String> imageUrls = imageService.getImageUrls(query);
                if (!imageUrls.isEmpty()) {
                    int idx = tick % imageUrls.size();
                    String imageUrl = imageUrls.get(idx);
                    Platform.runLater(() -> {
                        System.out.println("Updating live image (tick " + tick + "): " + imageUrl);
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

    private static void loadMatchingRoutesFromDatabase(ComboBox<String> routeBox, String experienceLevel) {
        RouteDAO routeDAO = new RouteDAO();
        List<Route> routes = routeDAO.getAllRoutes("user");
        for (Route route : routes) {
            if (isExperienceLevelMatching(experienceLevel, route.getDifficulty())) {
                routeBox.getItems().add(route.getName());
            }
        }
    }

    private static boolean isExperienceLevelMatching(String climberExperienceLevel, String routeDifficulty) {
        switch (climberExperienceLevel.toLowerCase()) {
            case "beginner":
                return "easy".equalsIgnoreCase(routeDifficulty);
            case "intermediate":
                return "easy".equalsIgnoreCase(routeDifficulty) || "moderate".equalsIgnoreCase(routeDifficulty);
            case "expert":
                return "easy".equalsIgnoreCase(routeDifficulty)
                        || "moderate".equalsIgnoreCase(routeDifficulty)
                        || "difficult".equalsIgnoreCase(routeDifficulty);
            default:
                return false;
        }
    }




    private static void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            String osrmUrl = String.format("http://router.project-osrm.org/route/v1/foot/%f,%f;%f,%f?overview=full&geometries=geojson",
                    startLon, startLat, endLon, endLat);
            HttpRequest osrmRequest = HttpRequest.newBuilder().uri(URI.create(osrmUrl)).build();
            HttpResponse<String> osrmResponse = client.send(osrmRequest, HttpResponse.BodyHandlers.ofString());
            JsonObject osrmJson = new Gson().fromJson(osrmResponse.body(), JsonObject.class);
            JsonArray routes = osrmJson.getAsJsonArray("routes");
            if (routes.size() == 0) {
                showMessage("Error", "No route found.");
                return;
            }
            JsonObject routeObj = routes.get(0).getAsJsonObject();
            double osrmDurationSec = routeObj.get("duration").getAsDouble();
            double osrmDistance = routeObj.get("distance").getAsDouble();
            int computedDurationHours = (int)(osrmDurationSec / 3600);
            int computedDurationMinutes = (int)((osrmDurationSec % 3600) / 60);
            double computedDistanceKm = osrmDistance / 1000.0;
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
            String jsCall = String.format("drawRoute(%s, %d, %d, %.1f)", jsArrayString, computedDurationHours, computedDurationMinutes, computedDistanceKm);
            Platform.runLater(() -> webEngine.executeScript(jsCall));
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error", "An error occurred while searching for the route.");
        }
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

    public static class expedition {
        public String name;
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
        return "45.0, 25.0";
    }

    public static Climber getLoggedInUser() {
        return me.ancastanoev.Application.getLoggedInUser();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
