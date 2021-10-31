package advisor.request;

import java.net.http.*;
import java.net.URI;
public abstract class AbstractController {

    public static final String NO_MORE_PAGES = "No more pages";

    protected String context;

    protected HttpClient httpClient;

    protected String accessToken;

    protected String nextURI;

    protected String previousURI;

    protected Integer pageSize = 5;

    public AbstractController() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public abstract void request(String... args);

    public abstract void next();

    public abstract void previous();

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
