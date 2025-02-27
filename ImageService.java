package me.ancastanoev.imageapi;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

public class ImageService {
    private static final String UNSPLASH_API_URL = "https://api.unsplash.com/search/photos";
    private static final String ACCESS_KEY = "SypvW_n1LBWUku0DJW5aYtHsdyFtpL44Anp7Kd1ZNFE";

    private final HttpClient httpClient;
    private final Gson gson;

    public ImageService() {
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();
    }


    public List<String> getImageUrls(String query) throws IOException, InterruptedException {
        List<String> imageUrls = new ArrayList<>();
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = UNSPLASH_API_URL + "?query=" + encodedQuery + "&client_id=" + ACCESS_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
            JsonArray results = jsonObject.getAsJsonArray("results");
            for (JsonElement element : results) {
                JsonObject photo = element.getAsJsonObject();
                JsonObject urls = photo.getAsJsonObject("urls");
                String imageUrl = urls.get("small").getAsString();
                imageUrls.add(imageUrl);
            }
        } else {
            System.err.println("Error fetching images: HTTP " + response.statusCode());
        }
        return imageUrls;
    }
}
