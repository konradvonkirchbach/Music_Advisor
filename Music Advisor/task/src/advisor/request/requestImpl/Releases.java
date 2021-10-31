package advisor.request.requestImpl;

import advisor.Server;
import advisor.request.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.logging.Logger;

import java.net.URI;
import java.net.http.*;

public class Releases extends Request {

    private static final Logger LOGGER = Logger.getLogger(Releases.class.toString());

    public static final String NAME = "new";

    public static final String PATH = "/v1/browse/new-releases";

    public Releases() {
        super();
    }

    public Releases(String context, String accessToken) {
        super(context, accessToken);
    }

    @java.lang.Override
    public String getName() {
        return NAME;
    }

    @java.lang.Override
    public String request(String... args) {
        try {
            LOGGER.info("Making request to " + context + PATH);
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
        JsonArray items = jsonBody.get("albums").getAsJsonObject()
                .get("items").getAsJsonArray();

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            builder.append(processItem(items.get(i).getAsJsonObject()));
        }

        return builder.toString();
    }

    private String processItem(JsonObject album) {
        StringBuilder builder = new StringBuilder();
        builder.append(album.get("name").getAsString())
                .append("\n");
        builder.append("[");

        JsonArray artists = album.get("artists").getAsJsonArray();
        for (int i = 0; i < artists.size(); i++) {
            JsonObject artist = artists.get(i).getAsJsonObject();
            builder.append(artist.get("name").getAsString());
            if (i < artists.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append("]").append("\n");
        builder.append(album.get("external_urls").getAsJsonObject().get("spotify").getAsString());
        builder.append("\n\n");

        return builder.toString();
    }
}
