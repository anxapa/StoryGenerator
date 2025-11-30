package service;

import com.google.genai.Client;
import com.google.genai.types.*;
import config.Config;
import model.Story;
import model.StoryCharacter;
import org.json.JSONObject;

public class GeminiAPIService {
    private final Client client;
    private final Schema storySchema;

    public GeminiAPIService() {
        client = Client.builder().apiKey(Config.GEMINI_API_KEY).build();
        storySchema = Story.generateSchema();
    }

    /**
     * Checks for the connection with Gemini API.
     * @return true if authentication is successful, else false
     */
    public boolean authenticate() {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash-lite",
                        "Please reply with just \"yes\". No other words should be included.",
                        null);

        return response.text().equals("yes");
    }

    /**
     * Sends a request to Gemini.
     * @param prompt - prompt to generate with
     * @return response text
     */
    public String call(String prompt) {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash-lite",
                        prompt,
                        null);

        return response.text() + "\nEND\n";
    }

    /**
     * Extracts a story through the GeminiAPI to JSON.
     * @param story - story to be extracted
     * @return JSONObject representation of story
     */
    public String extractStory(String story) {
        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .responseSchema(storySchema)
                        .build();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash-lite",
                        String.format("Extract the story: %s", story),
                        config);

        return response.text() + "\nEND\n";
    }

    public static void main(String[] args) {
        GeminiAPIService geminiAPI = new GeminiAPIService();
        String response = geminiAPI.call("Generate a JSON for a character with the values: 'name', 'gender'" +
                        ", 'age', 'species', 'race', 'description'. The description should just be a short description with no reference" +
                        "to the values of the JSON. There should be no other response other than the JSON. Please remove the backticks" +
                        ", newlines, and spaces for the JSON.");
        System.out.println(response);
        StoryCharacter character = StoryCharacter.fromJSON(new JSONObject(response));
        System.out.println(character);
    }
}