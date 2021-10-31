package advisor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.logging.Logger;
import java.time.*;

public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.toString());

    private static final String ERROR_RESPONSE = "Authorization code not found. Try again.";

    private static final String SUCCESS_RESPONSE = "Got the code. Return back to your program.";

    private HttpServer server;

    private String response;

    public Server() throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);
        server.createContext("/",
            new HttpHandler() {
                @java.lang.Override
                public void handle(HttpExchange exchange) throws IOException {
                    String query = null;
                    while (query == null) {
                        query = exchange.getRequestURI().getQuery();
                        response = query;
                        LOGGER.info("SERVER: request URI " + response);
                        if (isError()) {
                            LOGGER.info("SERVER: request URI is error");
                            exchange.sendResponseHeaders(200, ERROR_RESPONSE.length());
                            exchange.getResponseBody().write(ERROR_RESPONSE.getBytes());
                            exchange.getResponseBody().close();
                        } else {
                            LOGGER.info("SERVER: request URI is valid");
                            exchange.sendResponseHeaders(200, SUCCESS_RESPONSE.length());
                            exchange.getResponseBody().write(SUCCESS_RESPONSE.getBytes());
                            exchange.getResponseBody().close();
                        }
                    }
                }
            });
    }

    public boolean isError() {
        return response == null ? true : response.startsWith("error");
    }

    public String getCode() {
        if (!isError()) {
            return response.split("=")[1];
        } else {
            throw new IllegalStateException("Did not manage to aquire code");
        }
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1);
    }
}
