package advisor;

import java.net.http.*;
import java.net.URI;
import java.io.IOException;
import java.util.*;

public class Client {

    private HttpClient httpClient;
    private String _code;
    private String _uri;

    public Client(String uri, String code) {
        _code = code;
        _uri = uri;
        httpClient = HttpClient.newBuilder().build();
    }

    public String getResponse() {
        StringBuilder body = new StringBuilder("grant_type=authorization_code");
        body.append("&code=").append(_code);
        body.append("&redirect_uri=http://localhost:8080&response_type=code");
        body.append("&client_id=cfb6165a0fc245a18a1a83e02538a52b");
        body.append("&client_secret=13e311aa34df4e6a8ccad99f30c27692");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .uri(URI.create(_uri + "/api/token"))
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            System.out.println("Making http request for access_token...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getEncodedString(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes());
    }
}
