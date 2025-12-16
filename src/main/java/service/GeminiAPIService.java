package service;

import com.google.genai.Client;
import com.google.genai.errors.ApiException;
import com.google.genai.types.*;
import config.Config;
import model.Story;

public class GeminiAPIService {
    private final Client client;
    private final Schema storySchema;
    private String currentModel = availableModels[1];

    public final static String[] availableModels = {
            "gemini-2.5-flash",
            "gemini-2.5-flash-lite",
            "gemini-2.5-pro",
            "gemini-3-pro-preview",
    };

    public GeminiAPIService() {
        client = Client.builder().apiKey(Config.getGeminiApiKey()).build();
        storySchema = Story.generateSchema();
    }

    /**
     * Sets the current model to the given one.
     * @param currentModel - name of model
     */
    public void setCurrentModel(String currentModel) {
        this.currentModel = currentModel;
    }

    /**
     * Requests a standard call to Gemini.
     * @param prompt - prompt to generate with
     * @return response text
     */
    public String call(String prompt) {
        return sendRequest(currentModel, prompt, null);
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

        return sendRequest("gemini-2.5-flash-lite", story, config);
    }

    /**
     * Sends the request to Gemini.
     * @param model - Gemini model to use
     * @param prompt - text prompt
     * @param config - GenerateContentConfig
     * @return response
     */
    private String sendRequest(String model, String prompt, GenerateContentConfig config) {
        String responseText;

        try {
            GenerateContentResponse response =
                    client.models.generateContent(
                            model,
                            prompt,
                            config);

            responseText = response.text();
        } catch (ApiException e) {
            responseText = "!ERROR: " + e.getMessage();
        }

        return responseText;
    }
}