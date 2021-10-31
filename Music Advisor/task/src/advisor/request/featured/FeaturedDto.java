package advisor.request.featured;

import com.google.gson.JsonObject;

public class FeaturedDto {

    private String name;

    private String href;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append("\n");
        builder.append(href).append("\n");
        return builder.toString();
    }

    public static FeaturedDto builder(JsonObject jo) {
        FeaturedDto featuredDto = new FeaturedDto();
        featuredDto.name = jo.get("name").getAsString();
        featuredDto.href = jo.get("external_urls").getAsJsonObject()
                .get("spotify").getAsString();
        return featuredDto;
    }

}
