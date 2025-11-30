package model;

import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class Story {
    private String name;
    private Hashtable<String, StoryCharacter> characterDict;
    private Hashtable<String, StoryLocation> locationDict;
    private String summary;
    private String story;

    public Story(String name) {
        this.name = name;
        this.characterDict = new Hashtable<>();
        this.locationDict = new Hashtable<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Returns a sorted list of character names from the story.
     * @return sorted list of character names
     */
    public List<String> getCharacterNames() {
        // Get the key set from the character hashtable and turn that into a sorted list
        Set<String> characterNameSet = characterDict.keySet();
        List<String> characterNameList = new ArrayList<>(characterNameSet);
        Collections.sort(characterNameList);

        return characterNameList;
    }

    /**
     * Returns a sorted list of location names from the story.
     * @return sorted list of location names
     */
    public List<String> getLocationNames() {
        // Get the key set from the location hashtable and turn that into a sorted list
        Set<String> locationNameSet = locationDict.keySet();
        List<String> locationNameList = new ArrayList<>(locationNameSet);
        Collections.sort(locationNameList);

        return locationNameList;
    }

    /**
     * Adds a character to the story. If the character is already present, then override the old character
     * with the new one.
     * @param character - character to add
     */
    public void addCharacter(StoryCharacter character) {
        characterDict.put(character.getName(), character);
    }

    /**
     * Adds a location to the story. If the location is already present, then override the old location
     * with the new one.
     * @param location - location to add
     */
    public void addLocation(StoryLocation location) {
        locationDict.put(location.getName(), location);
    }

    /**
     * Returns a character from the story.
     * @param name character name to get
     * @return StoryCharacter object
     */
    public StoryCharacter getCharacter(String name) {
        // If the character is not found then return null
        if (!characterDict.containsKey(name)) {
            return null;
        }

        return characterDict.get(name);
    }

    /**
     * Returns a location from the story.
     * @param name location name to get
     * @return StoryLocation object
     */
    public StoryLocation getLocation(String name) {
        // If the location is not found then return null
        if (!locationDict.containsKey(name)) {
            return null;
        }

        return locationDict.get(name);
    }

    /**
     * Removes character from the story.
     * @param name character name to remove
     * @return removed StoryCharacter object
     */
    public StoryCharacter removeCharacter(String name) {
        // If the character is not found then return null
        if (!characterDict.containsKey(name)) {
            return null;
        }

        // Remove character by replacing it with null.
        StoryCharacter character = characterDict.get(name);
        characterDict.put(name, null);

        return character;
    }

    /**
     * Removes location from the story.
     * @param name location name to remove
     * @return removed StoryLocation object
     */
    public StoryLocation removeLocation(String name) {
        // If the location is not found then return null
        if (!locationDict.containsKey(name)) {
            return null;
        }

        // Remove location by replacing it with null.
        StoryLocation location = locationDict.get(name);
        locationDict.put(name, null);

        return location;
    }

    /**
     * Returns a JSONObject with all the story's values.
     * @return JSONObject of the story
     */
    public JSONObject toJSON() {
        JSONObject storyJSON = new JSONObject();

        storyJSON.put("name", getName());
        storyJSON.put("summary", getSummary());
        storyJSON.put("story", getStory());

        // Characters
        JSONArray characterArrayJSON = new JSONArray();
        for (String characterName: getCharacterNames()) {
            StoryCharacter character = getCharacter(characterName);
            characterArrayJSON.put(character.toJSON());
        }

        // Locations
        JSONArray locationArrayJSON = new JSONArray();
        for (String locationName: getLocationNames()) {
            StoryLocation location = getLocation(locationName);
            locationArrayJSON.put(location.toJSON());
        }

        // Add characters and locations to JSON
        storyJSON.put("characters", characterArrayJSON);
        storyJSON.put("locations", locationArrayJSON);

        return storyJSON;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Story Name: %s\n\n", name));
        sb.append(String.format("Summary: %s\n\n", summary));

        sb.append("Characters: \n");
        for (String name: getCharacterNames()) {
            sb.append(String.format("\t- %s\n", name));
        }
        sb.append("\n");

        sb.append("Locations: \n");
        for (String name: getLocationNames()) {
            sb.append(String.format("\t- %s\n", name));
        }
        sb.append("\n");

        sb.append(String.format("Story: \n\t%s\n", story));

        return sb.toString();
    }

    /**
     * Returns a Story object from a JSON file.
     * @param storyJSON JSONObject
     * @return Story object
     */
    public static Story fromJSON(JSONObject storyJSON) {
        String name = storyJSON.getString("name");
        String summary = storyJSON.getString("summary");
        JSONArray characterArray = storyJSON.getJSONArray("characters");
        JSONArray locationArray = storyJSON.getJSONArray("locations");

        Story story = new Story(name);

        // May or may not have a story set in already
        try {
            String storyText = storyJSON.getString("story");
            story.setStory(storyText);
        } catch (JSONException e) {
            System.out.printf("WARNING: Story \"%s\" does not have a value \"story\" inside it. \n", name);
        }

        story.setSummary(summary);

        // Get characters
        for (int i = 0; i < characterArray.length(); i++) {
            StoryCharacter character = StoryCharacter.fromJSON(characterArray.getJSONObject(i));
            story.addCharacter(character);
        }

        // Get locations
        for (int i = 0; i < locationArray.length(); i++) {
            StoryLocation location = StoryLocation.fromJSON(locationArray.getJSONObject(i));
            story.addLocation(location);
        }

        return story;
    }

    /**
     * Returns a Schema object of the Story class
     * @return Schema object
     */
    public static Schema generateSchema() {
        Schema characterSchema = StoryCharacter.generateSchema();
        Schema locationSchema = StoryLocation.generateSchema();

        Schema characterListSchema = Schema.builder()
                .type(Type.Known.ARRAY)
                .items(characterSchema)
                .build();

        Schema locationListSchema = Schema.builder()
                .type(Type.Known.ARRAY)
                .items(locationSchema)
                .build();

        Map<String, Schema> storyMap = new HashMap<>();
        storyMap.put("name", Schema.builder().type(Type.Known.STRING).build());
        storyMap.put("summary", Schema.builder().type(Type.Known.STRING).build());
        storyMap.put("characters", characterListSchema);
        storyMap.put("locations", locationListSchema);

        Schema storySchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(storyMap)
                .required("name", "summary", "characters", "locations")
                .build();

        return storySchema;
    }
}
