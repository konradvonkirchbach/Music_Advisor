package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;
import java.io.IOException;

public class Main {

    private static String authUrlParams = "/authorize?client_id=cfb6165a0fc245a18a1a83e02538a52b&redirect_uri=http://localhost:8080&response_type=code";

    private String _accessPoint;

    private String _resource;

    private String _accessToken;

    private Server _server;

    public static void main(String[] args) throws IOException {
        Main m = new Main();
        m.getAccessPoint(args);
        m.getResource(args);
        m.getPageSize(args);
        String input = "";
        Scanner scanner = new Scanner(System.in);
        boolean isAuthenticated = false;
        System.out.print("> ");
        while (scanner.hasNext()) {
            input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("---GOODBYE!---");
                break;
            } else if ("auth".equalsIgnoreCase(input)) {
                isAuthenticated = m.authorize();
            } else if (isAuthenticated) {
                RequestFactory.makeRequest(input);
            } else {
                System.out.println("Please, provide access for application.");
            }
            System.out.print("> ");
        }
    }

    private void getAccessPoint(String... args) {
        _accessPoint = "https://accounts.spotify.com";
        for (int i = 0; i < args.length - 1; i++) {
            if ("-access".equals(args[i])) {
                _accessPoint = args[i + 1];
            }
        }
    }

    private void getResource(String... args) {
        _resource = "https://api.spotify.com";
        for (int i = 0; i < args.length - 1; i++) {
            if ("-resource".equals(args[i])) {
                _resource = args[i + 1];
            }
        }
        RequestFactory.resource = _resource;
    }

    private void getPageSize(String... args) {
        Integer pageSize = 5;
        for (int i = 0; i < args.length - 1; i++) {
            if ("-page".equals(args[i])) {
                pageSize = Integer.parseInt(args[i + 1]);
            }
        }
        RequestFactory.pageSize = pageSize;
    }

    private boolean authorize() throws IOException {
        boolean isAuthenticated = false;
        _server = new Server();
        _server.start();
        System.out.println(_accessPoint + authUrlParams);
        System.out.println("waiting for code...");
        try {
            Thread.sleep(5_000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Client client = new Client(_accessPoint, _server.getCode());

        String response = client.getResponse();

        JsonObject jo = JsonParser.parseString(response).getAsJsonObject();

        _accessToken = jo.get("access_token").getAsString();

        RequestFactory.accessToken = _accessToken;

        if (!_server.isError()) {
            isAuthenticated = true;
            System.out.println("Success!");
        } else {
            System.out.println("Something went wrong during authorization");
        }
        _server.stop();
        return isAuthenticated;
    }
}
