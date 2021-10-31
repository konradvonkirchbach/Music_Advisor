package advisor.request.requestImpl;

import advisor.request.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.*;

public class Categories extends Request {

    public static final String NAME = "categories";

    public static final String PATH = "/v1/browse/categories";

    public Categories() {
        super();
    }

    public Categories(String context, String accessToken) {
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
        JsonArray categories = jsonBody.get("categories").getAsJsonObject()
                .get("items").getAsJsonArray();

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < categories.size(); i++) {
            builder.append(categories.get(i).getAsJsonObject().get("name").getAsString())
                    .append("\n");
        }

        return builder.toString();
    }
}
