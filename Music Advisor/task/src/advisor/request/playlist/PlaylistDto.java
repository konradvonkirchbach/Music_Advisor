package advisor.request.playlist;

import com.google.gson.JsonObject;

public class PlaylistDto {

    private String name;

    private String href;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append("\n");
        builder.append(href).append("\n");
        return builder.toString();
    }

    public static PlaylistDto builder(JsonObject jo) {
        PlaylistDto playlistDto = new PlaylistDto();
        playlistDto.name = jo.get("name").getAsString();
        playlistDto.href = jo.get("external_urls").getAsJsonObject()
                .get("spotify").getAsString();
        return playlistDto;
    }

}
