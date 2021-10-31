package advisor.request.playlist;

import advisor.request.AbstractController;
import advisor.request.Model;
import advisor.request.category.CategoryController;
import advisor.request.requestImpl.Categories;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.*;
import java.net.URI;
import java.util.*;

public class PlaylistController extends AbstractController {

    public static final String PATH = "/v1/browse/categories/%s/playlists?limit=%d";

    private Model<PlaylistDto> model;

    public PlaylistController(Integer pageSize) {
        this.pageSize = pageSize;
        model = new Model<>(pageSize);
    }

    @java.lang.Override
    public void request(String... args) {
        StringBuilder categoryName = new StringBuilder();
        for (String s : args) {
            categoryName.append(s)
                    .append(" ");
        }
        String name = categoryName.toString();
        String id = getId(name.substring(0, name.length() - 1));
        if (id == null) {
            System.out.println("Unknown category name\n");
            return;
        }
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(String.format(context + PATH, id, pageSize)))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (processBody(response.body())) {
                System.out.println(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean processBody(String body) {
        JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();

        if (jsonBody.has("error")) {
            System.out.println(jsonBody.get("error").getAsJsonObject().get("message").getAsString());
            return false;
        }

        JsonObject playlists = JsonParser.parseString(body).getAsJsonObject()
                .get("playlists").getAsJsonObject();

        // next URIs
        if (!playlists.get("next").isJsonNull()) {
            nextURI = playlists.get("next").getAsString();
        } else {
            nextURI = null;
        }

        if (!playlists.get("previous").isJsonNull()) {
            previousURI = playlists.get("previous").getAsString();
        } else {
            previousURI = null;
        }

        // pagination
        Integer totalNumberOfElements = playlists.get("total").getAsInt();
        Integer offset = playlists.get("offset").getAsInt();

        model.setNumberOfPages((int) Math.ceil((double) totalNumberOfElements / pageSize));
        model.setCurrentPage(offset / pageSize + 1);

        // model
        List<PlaylistDto> newContent = new ArrayList<>();
        JsonArray items = playlists.get("items").getAsJsonArray();
        for (int i = 0; i < items.size(); i++) {
            newContent.add(PlaylistDto.builder(items.get(i).getAsJsonObject()));
        }
        model.setContent(newContent);
        return true;
    }

    @java.lang.Override
    public void next() {
        if (nextURI == null) {
            System.out.println(NO_MORE_PAGES);
        } else {
            try {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .header("Authorization", "Bearer " + accessToken)
                        .uri(URI.create(nextURI))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                processBody(response.body());
                System.out.println(model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @java.lang.Override
    public void previous() {
        if (previousURI == null) {
            System.out.println(NO_MORE_PAGES);
        } else {
            try {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .header("Authorization", "Bearer " + accessToken)
                        .uri(URI.create(previousURI))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                processBody(response.body());
                System.out.println(model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getId(String categoryName) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(context + CategoryController.PATH))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return extractId(response.body(), categoryName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractId(String body, String categoryName) {
        JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
        JsonArray categories = jsonBody.get("categories").getAsJsonObject()
                .get("items").getAsJsonArray();

        for (int i = 0; i < categories.size(); i++) {
            JsonObject category = categories.get(i).getAsJsonObject();
            if (category.get("name").getAsString().equalsIgnoreCase(categoryName)) {
                return category.get("id").getAsString();
            }
        }

        return null;
    }
}
