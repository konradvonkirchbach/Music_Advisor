package advisor.request.featured;

import advisor.request.AbstractController;
import advisor.request.Model;
import advisor.request.requestImpl.Featured;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.*;
import java.util.*;

public class FeaturedController extends AbstractController {

    public static final String PATH = "/v1/browse/featured-playlists";

    private Model<FeaturedDto> model;

    public FeaturedController(Integer pageSize) {
        this.pageSize = pageSize;
        model = new Model<>(pageSize);
    }

    @java.lang.Override
    public void request(String... args) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(String.format("%s%s?limit=%d", context, PATH, pageSize)))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            processBody(response.body());
            System.out.println(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void processBody(String body) {
        JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
        JsonObject playLists = jsonBody.get("playlists").getAsJsonObject();

        // next URIs
        if (!playLists.get("next").isJsonNull()) {
            nextURI = playLists.get("next").getAsString();
        } else {
            nextURI = null;
        }

        if (!playLists.get("previous").isJsonNull()) {
            previousURI = playLists.get("previous").getAsString();
        } else {
            previousURI = null;
        }

        // pagination
        Integer totalNumberOfElements = playLists.get("total").getAsInt();
        Integer offset = playLists.get("offset").getAsInt();

        model.setNumberOfPages((int) Math.ceil((double) totalNumberOfElements / pageSize));
        model.setCurrentPage(offset / pageSize + 1);

        // content
        List<FeaturedDto> newContent = new ArrayList<>();
        JsonArray items = playLists.get("items").getAsJsonArray();
        for (int i = 0; i < items.size(); i++) {
            newContent.add(FeaturedDto.builder(items.get(i).getAsJsonObject()));
        }
        model.setContent(newContent);
    }
}
