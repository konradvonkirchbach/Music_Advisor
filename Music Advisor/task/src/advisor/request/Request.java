package advisor.request;

import java.net.http.*;

public abstract class Request {

    protected String context;

    protected HttpClient httpClient;

    protected String accessToken;

    public Request() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    public Request(String context, String accessToken) {
        this.context = context;
        this.accessToken = accessToken;
        this.httpClient = HttpClient.newBuilder().build();
    }

    public abstract String getName();

    public abstract String request(String... args);

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
