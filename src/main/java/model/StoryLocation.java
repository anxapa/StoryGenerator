package model;

import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StoryLocation {
    private String name;
    private String description;

    public StoryLocation(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns a JSONObject with all the location's values.
     * @return JSONObject of the location
     */
    public JSONObject toJSON() {
        JSONObject locationJSON = new JSONObject();

        locationJSON.put("name", getName());
        locationJSON.put("description", getDescription());

        return locationJSON;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Location Name: %s\n\n", name));
        sb.append(String.format("Description:\n\t%s\n", description));

        return sb.toString();
    }

    /**
     * Returns a StoryLocation object from a JSON file.
     * @param locationJSON JSONObject
     * @return StoryLocation object
     */
    public static StoryLocation fromJSON(JSONObject locationJSON) {
        String name = locationJSON.getString("name");
        String description = locationJSON.getString("description");

        return new StoryLocation(name, description);
    }

    /**
     * Returns a Schema object of the StoryLocation class
     * @return Schema object
     */
    public static Schema generateSchema() {
        Map<String, Schema> locationMap = new HashMap<>();
        locationMap.put("name", Schema.builder().type(Type.Known.STRING).build());
        locationMap.put("description", Schema.builder().type(Type.Known.STRING).build());

        Schema locationSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(locationMap)
                .required("name", "description")
                .build();

        return locationSchema;
    }
}
