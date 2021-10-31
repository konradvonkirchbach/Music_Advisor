package advisor;

import advisor.request.AbstractController;
import advisor.request.category.CategoryController;
import advisor.request.featured.FeaturedController;
import advisor.request.newrealeases.NewController;
import advisor.request.playlist.PlaylistController;

import java.util.*;

public class RequestFactory {

    public static String resource;

    public static String accessToken;

    public static Integer pageSize;

    private static Map<String, AbstractController> CONTROLLERS;

    private static AbstractController controller;

    public static void init() {
        if (CONTROLLERS == null) {
            CONTROLLERS = Map.ofEntries(
                    Map.entry("categories", new CategoryController(pageSize)),
                    Map.entry("new", new NewController(pageSize)),
                    Map.entry("featured", new FeaturedController(pageSize)),
                    Map.entry("playlist", new PlaylistController(pageSize))
            );
        }
    }

    public static void makeRequest(String requestType) {
        init();
        String[] typeAndParams = requestType.split("\\s+");
        String type = typeAndParams[0];

        if ("next".equalsIgnoreCase(type)) {
            controller.next();
        } else if ("previous".equalsIgnoreCase(type)) {
            controller.previous();
        } else if (CONTROLLERS.keySet().contains(type)) {
            controller = CONTROLLERS.get(type);
            controller.setAccessToken(accessToken);
            controller.setContext(resource);
            controller.request(Arrays.copyOfRange(typeAndParams, 1, typeAndParams.length));
        } else {
            System.out.println("Not recognized input");
        }
    }

}
