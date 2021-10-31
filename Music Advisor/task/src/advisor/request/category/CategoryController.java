package advisor.request.category;

import advisor.request.AbstractController;
import advisor.request.Model;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.*;
import java.net.URI;
import java.util.*;

public class CategoryController extends AbstractController {

    public static final String PATH = "/v1/browse/categories";

    private Model<CategoryDto> model;

    public CategoryController(Integer pageSize) {
        this.pageSize = pageSize;
        model = new Model<CategoryDto>(pageSize);
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
        JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject()
                .get("categories").getAsJsonObject();

        // next URIs
        if (!jsonBody.get("next").isJsonNull()) {
            nextURI = jsonBody.get("next").getAsString();
        } else {
            nextURI = null;
        }

        if (!jsonBody.get("previous").isJsonNull()) {
            previousURI = jsonBody.get("previous").getAsString();
        } else {
            previousURI = null;
        }

        // content
        JsonArray categories = jsonBody.get("items").getAsJsonArray();

        List<CategoryDto> categoriesList = new ArrayList<>();

        for (int i = 0; i < categories.size(); i++) {
            categoriesList.add(CategoryDto.buildFromJson(categories.get(i).getAsJsonObject()));
        }

        model.setContent(categoriesList);

        // pagination
        Integer totalNumberOfElements = jsonBody.get("total").getAsInt();
        Integer offset = jsonBody.get("offset").getAsInt();

        model.setNumberOfPages((int) Math.ceil((double) totalNumberOfElements / pageSize));
        model.setCurrentPage(offset / pageSize + 1);
    }

}
