package model;

import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StoryCharacter {
    private String name;
    private String race;
    private String species;
    private int age;
    private String gender;
    private String description;

    public StoryCharacter(String name, int age, String race, String species, String gender, String description) {
        this.description = description;
        this.gender = gender;
        this.species = species;
        this.age = age;
        this.race = race;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a JSONObject with all the character's values.
     * @return JSONObject of the character
     */
    public JSONObject toJSON() {
        JSONObject characterJSON = new JSONObject();

        characterJSON.put("name", getName());
        characterJSON.put("age", getAge());
        characterJSON.put("gender", getGender());
        characterJSON.put("race", getRace());
        characterJSON.put("species", getSpecies());
        characterJSON.put("description", getDescription());

        return characterJSON;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Character Name: %s\n\n", name));
        sb.append(String.format("Age: %d\n\n", age));
        sb.append(String.format("Gender: %s\n\n", gender));
        sb.append(String.format("Species: %s\n\n", species));
        sb.append(String.format("Race: %s\n\n", race));
        sb.append(String.format("Description:\n\t%s\n", description));

        return sb.toString();
    }

    /**
     * Returns a Character object from a JSON file.
     * @param characterJSON JSONObject
     * @return Character object
     */
    public static StoryCharacter fromJSON(JSONObject characterJSON) {
        String name = characterJSON.getString("name");
        int age = characterJSON.getInt("age");
        String race = characterJSON.getString("race");
        String species = characterJSON.getString("species");
        String gender = characterJSON.getString("gender");
        String description = characterJSON.getString("description");

        return new StoryCharacter(name, age, race, species, gender, description);
    }

    /**
     * Returns a Schema object of the StoryCharacter class
     * @return Schema object
     */
    public static Schema generateSchema() {
        Map<String, Schema> characterMap = new HashMap<>();
        characterMap.put("name", Schema.builder().type(Type.Known.STRING).build());
        characterMap.put("race", Schema.builder().type(Type.Known.STRING).build());
        characterMap.put("species", Schema.builder().type(Type.Known.STRING).build());
        characterMap.put("age", Schema.builder().type(Type.Known.NUMBER).build());
        characterMap.put("gender", Schema.builder().type(Type.Known.STRING).build());
        characterMap.put("description", Schema.builder().type(Type.Known.STRING).build());

        Schema characterSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(characterMap)
                .required("name", "race", "species", "age", "gender", "description")
                .build();

        return characterSchema;
    }
}
