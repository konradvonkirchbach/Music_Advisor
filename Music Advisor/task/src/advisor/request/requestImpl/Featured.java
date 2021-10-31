package advisor.request.requestImpl;

import advisor.request.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.*;

public class Featured extends Request {

    public static final String NAME = "featured";

    public static final String PATH = "/v1/browse/featured-playlists";

    public Featured() {
        super();
    }

    public Featured(String context, String accessToken) {
        super(context, accessToken);
    }

    @java.lang.Override
    public String getName() {
        return NAME;
    }

    @java.lang.Override
    public String request(String... args) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(context + PATH))
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
        JsonArray items = jsonBody.get("playlists").getAsJsonObject()
                .get("items")
                .getAsJsonArray();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            builder.append(processItem(items.get(i).getAsJsonObject()));
        }
        return builder.toString();
    }

    private String processItem(JsonObject featured) {
        StringBuilder builder = new StringBuilder();
        builder.append(featured.get("name").getAsString())
                .append("\n");
        builder.append(featured.get("external_urls").getAsJsonObject()
                .get("spotify").getAsString())
                .append("\n\n");
        return builder.toString();
    }
}
