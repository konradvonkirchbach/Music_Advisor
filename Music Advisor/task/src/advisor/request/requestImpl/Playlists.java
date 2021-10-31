package advisor.request.requestImpl;

import advisor.request.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.logging.Logger;

import java.net.URI;
import java.net.http.*;

public class Playlists extends Request {

    private static final Logger LOGGER = Logger.getLogger(Releases.class.toString());

    public static final String NAME = "playlists";

    public static final String PATH = "/v1/browse/categories/%s/playlists";

    public Playlists() {
        super();
    }

    public Playlists(String context, String accessToken) {
        super(context, accessToken);
    }

    @java.lang.Override
    public String getName() {
        return NAME;
    }

    @java.lang.Override
    public String request(String... args) {
        StringBuilder categoryName = new StringBuilder();
        for (String s : args) {
            categoryName.append(s)
                    .append(" ");
        }
        String name = categoryName.toString();
        LOGGER.info(name);
        String id = getId(name.substring(0, name.length() - 1));
        if (id == null) {
            return "Unknown category name\n";
        }
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(String.format(context + PATH, id)))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return processBody(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String processBody(String body) {
        JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();

        if (jsonBody.has("error")) {
            return jsonBody.get("error").getAsJsonObject().get("message").getAsString();
        }

        JsonArray items = jsonBody.get("playlists").getAsJsonObject()
                .get("items")
                .getAsJsonArray();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            JsonObject jo = items.get(i).getAsJsonObject();
            builder.append(jo.get("name").getAsString())
                    .append("\n");
            builder.append(jo.get("external_urls")
                            .getAsJsonObject()
                            .get("spotify")
                            .getAsString())
                    .append("\n\n");
        }

        return builder.toString();
    }

    private String getId(String categoryName) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(context + Categories.PATH))
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
