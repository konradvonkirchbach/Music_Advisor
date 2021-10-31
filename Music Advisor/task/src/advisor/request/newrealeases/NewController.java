package advisor.request.newrealeases;

import advisor.request.AbstractController;
import advisor.request.Model;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.*;
import java.net.URI;
import java.util.*;

public class NewController extends AbstractController {

    public static final String PATH = "/v1/browse/new-releases";

    private Model<NewDto> model;

    public NewController(Integer pageSize) {
        this.pageSize = pageSize;
        model = new Model<NewDto>(pageSize);
    }

    @java.lang.Override
    public void request(String... args) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .header("limit", pageSize.toString())
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
        JsonObject albums = jsonBody.get("albums").getAsJsonObject();

        // next URIs
        if (!albums.get("next").isJsonNull()) {
            nextURI = albums.get("next").getAsString();
        } else {
            nextURI = null;
        }

        if (!albums.get("previous").isJsonNull()) {
            previousURI = albums.get("previous").getAsString();
        } else {
            previousURI = null;
        }

        // content
        JsonArray releases = albums.get("items").getAsJsonArray();
        List<NewDto> newList = new ArrayList<>();
        for (int i = 0; i < releases.size(); i++) {
            newList.add(NewDto.NewDtoBuilder(releases.get(i).getAsJsonObject()));
        }
        model.setContent(newList);

        // pagination
        Integer totalNumberOfElements = albums.get("total").getAsInt();
        Integer offset = albums.get("offset").getAsInt();

        model.setNumberOfPages((int) Math.ceil((double) totalNumberOfElements / pageSize));
        model.setCurrentPage(offset / pageSize + 1);
    }

}
