package advisor.request.newrealeases;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

public class NewDto {

    private String name;

    private List<String> artists = new ArrayList<>();

    private String href;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append("\n");
        builder.append(artists.toString()).append("\n");
        builder.append(href).append("\n");
        return builder.toString();
    }

    public static NewDto NewDtoBuilder(JsonObject jsonObject) {
        NewDto newDto = new NewDto();
        newDto.name = jsonObject.get("name").getAsString();

        JsonArray artistsArray = jsonObject.get("artists").getAsJsonArray();
        for (int i = 0; i < artistsArray.size(); i++) {
            JsonObject artist = artistsArray.get(i).getAsJsonObject();
            newDto.artists.add(artist.get("name").getAsString());
        }

        newDto.href = jsonObject.get("external_urls")
                .getAsJsonObject()
                .get("spotify")
                .getAsString();

        return newDto;
    }

}