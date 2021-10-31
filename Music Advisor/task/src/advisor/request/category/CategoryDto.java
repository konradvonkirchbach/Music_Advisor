package advisor.request.category;

import com.google.gson.JsonObject;

public class CategoryDto {

    private String name;

    @java.lang.Override
    public String toString() {
        return name;
    }

    public static CategoryDto buildFromJson(JsonObject json) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.name = json.get("name").getAsString();
        return categoryDto;
    }
}
