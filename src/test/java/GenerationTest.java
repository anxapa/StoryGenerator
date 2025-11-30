import config.Config;
import controller.SaveLoadHandler;
import controller.StoryGeneratorServer;
import model.Story;
import model.StoryCharacter;
import model.StoryLocation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import service.GeminiAPIService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class GenerationTest {
    /**
     * Tests if the connection with Gemini API is working
     */
    @Test
    void authenticateGeminiAPI() {
        GeminiAPIService api = new GeminiAPIService();
        assertTrue(api.authenticate());
    }

    /**
     * Tests if the server can run.
     */
    @Test
    void canServerRun() {
        StoryGeneratorServer server = new StoryGeneratorServer(Config.SERVER_PORT);
        boolean canRun = false;
        try {
            canRun = server.testRun();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            canRun = false;
        }

        assertTrue(canRun);
    }

    /**
     * Tests if JSON representation of StoryLocation can be correctly turned into StoryLocation objects.
     */
    @Test
    void storyLocationFromJSONTest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "Location");
        jsonObject.put("description", "Description");

        StoryLocation location = StoryLocation.fromJSON(jsonObject);

        assertEquals(location.getName(), jsonObject.getString("name"));
        assertEquals(location.getDescription(), jsonObject.getString("description"));
    }

    /**
     * Tests if StoryLocation objects can be turned into JSON's correctly.
     */
    @Test
    void storyLocationToJSONTest() {
        StoryLocation location = new StoryLocation("Random place", "Some random place");
        JSONObject locationJSON = location.toJSON();

        assertEquals(location.getName(), locationJSON.getString("name"));
        assertEquals(location.getDescription(), locationJSON.getString("description"));
    }

    /**
     * Tests if JSON representation of StoryCharacter can be correctly turned into StoryCharacter objects.
     */
    @Test
    void storyCharacterFromJSONTest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "Random guy");
        jsonObject.put("age", 20);
        jsonObject.put("gender", "Male");
        jsonObject.put("race", "Alien");
        jsonObject.put("species", "Unknown");
        jsonObject.put("description", "Strange little guy");

        StoryCharacter character = StoryCharacter.fromJSON(jsonObject);

        assertEquals(character.getName(), jsonObject.getString("name"));
        assertEquals(character.getAge(), jsonObject.getInt("age"));
        assertEquals(character.getGender(), jsonObject.getString("gender"));
        assertEquals(character.getRace(), jsonObject.getString("race"));
        assertEquals(character.getSpecies(), jsonObject.getString("species"));
        assertEquals(character.getDescription(), jsonObject.getString("description"));
    }

    /**
     * Tests if StoryCharacter objects can be turned into JSON's correctly.
     */
    @Test
    void storyCharacterToJSONTest() {
        StoryCharacter character = new StoryCharacter("Random elf", 100, "Wood Elf", "Elf",
                "Female", "A simple little elf");
        JSONObject characterJSON = character.toJSON();

        assertEquals(character.getName(), characterJSON.getString("name"));
        assertEquals(character.getAge(), characterJSON.getInt("age"));
        assertEquals(character.getRace(), characterJSON.getString("race"));
        assertEquals(character.getSpecies(), characterJSON.getString("species"));
        assertEquals(character.getGender(), characterJSON.getString("gender"));
        assertEquals(character.getDescription(), characterJSON.getString("description"));
    }

    /**
     * Tests if JSON representation of Story can be correctly turned into Story objects.
     */
    @Test
    void storyFromJSONTest() {
        JSONObject storyJSON = new JSONObject();
        storyJSON.put("name", "A New Story");
        storyJSON.put("summary", "Some story");
        storyJSON.put("story", "Long long story");

        JSONArray characters = new JSONArray();
        JSONArray locations = new JSONArray();
        for (int i = 0; i < 3; i++) {
            JSONObject characterJSON = new JSONObject();
            characterJSON.put("name", "Random guy " + (i + 1));
            characterJSON.put("age", 20);
            characterJSON.put("gender", "Male");
            characterJSON.put("race", "Alien");
            characterJSON.put("species", "Unknown");
            characterJSON.put("description", "Strange little guy");
            characters.put(characterJSON);

            JSONObject locationJSON = new JSONObject();
            locationJSON.put("name", "Location " + (i + 1));
            locationJSON.put("description", "Description");
            locations.put(locationJSON);
        }
        storyJSON.put("characters", characters);
        storyJSON.put("locations", locations);

        Story story = Story.fromJSON(storyJSON);

        assertEquals(story.getName(), storyJSON.getString("name"));
        assertEquals(story.getSummary(), storyJSON.getString("summary"));
        assertEquals(story.getStory(), storyJSON.getString("story"));
        assertEquals(story.getLocation(story.getLocationNames().get(1)).getName(),
                ((JSONObject) locations.get(1)).getString("name"));
        assertEquals(story.getCharacter(story.getCharacterNames().get(1)).getName(),
                ((JSONObject) characters.get(1)).getString("name"));
    }

    /**
     * Tests if Story objects can be turned into JSON's correctly.
     */
    @Test
    void storyToJSONTest() {
        Story story = new Story("An Old Story");
        story.setSummary("This is an old story");
        story.setStory("Some really long narrative");

        for (int i = 0; i < 3; i++) {
            StoryCharacter character = new StoryCharacter("Random elf " + (i + 1), 100, "Wood Elf", "Elf",
                    "Female", "A simple little elf");
            story.addCharacter(character);

            StoryLocation location = new StoryLocation("Random place " + (i + 1), "Some random place");
            story.addLocation(location);
        }

        JSONObject storyJSON = story.toJSON();

        assertEquals(story.getName(), storyJSON.getString("name"));
        assertEquals(story.getStory(), storyJSON.getString("story"));
        assertEquals(story.getSummary(), storyJSON.getString("summary"));
        assertEquals(story.getCharacterNames().size(), storyJSON.getJSONArray("characters").length());
        assertEquals(story.getLocationNames().size(), storyJSON.getJSONArray("locations").length());
    }

    /**
     * Tests if saving stories function is working.
     */
    @Test
    void saveTest() {
        String name = "Test";
        SaveLoadHandler handler = SaveLoadHandler.getInstance();
        int i = 0;
        File file;

        // Check if file exists
        // If yes, then increment i to keep finding for a name not used by a file.
        if ((file = new File(Config.PATH_TO_SAVE_FILE + "/" + name + i + ".json")).isFile()) {
            i++;
        }

        // Save story
        handler.saveStory(new Story(name + i));

        // Check if file exists
        assertTrue(file.isFile());

        // Delete the file
        if (file.isFile())
            file.delete();
    }

    /**
     * Tests if loading stories function is working.
     */
    @Test
    void loadTest() throws IOException {
        String name = "Test";
        SaveLoadHandler handler = SaveLoadHandler.getInstance();
        int i = 0;
        File file;

        // Check if file exists
        // If yes, then increment i to keep finding for a name not used by a file.
        if ((file = new File(Config.PATH_TO_SAVE_FILE + "/" + name + i + ".json")).isFile()) {
            i++;
        }

        // Manually create the JSON Object
        Story story = new Story(name + i);
        story.setStory("Story");
        story.setSummary("Summary");
        JSONObject storyJSON = story.toJSON();

        // Create the JSON File and put the contents there
        // Create File/BufferedWriter to write to file
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        // Print to file
        bw.write(storyJSON.toString(4));

        // Close BufferedWriter
        bw.close();

        // Check if the SaveLoadHandler can load in the test story
        handler.loadStories();
        assertEquals("Story", handler.loadStory(name + i).getStory());

        // Remove file
        file.delete();
    }
}
