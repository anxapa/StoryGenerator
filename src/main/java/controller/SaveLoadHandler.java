package controller;

import config.Config;
import model.Story;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

// Singleton class
public class SaveLoadHandler {
    private static final Hashtable<String, Story> storyDict = new Hashtable<>();
    private static volatile SaveLoadHandler instance;

    // Private constructor to prevent external initialization
    private SaveLoadHandler() {
        loadStories();
    }

    /**
     * Gets the current instance of the SaveLoadHandler singleton.
     * @return instance
     */
    public static synchronized SaveLoadHandler getInstance() {
        if (instance == null)
            instance = new SaveLoadHandler();

        return instance;
    }

    /**
     * Loads all the stories from a directory.
     */
    public void loadStories() {
        // Removes all stories from the dictionary
        storyDict.clear();

        File dir = new File(Config.PATH_TO_SAVE_FILE);
        for (File file : dir.listFiles()) {
            String rawText = "";

            // Reading the file
            try (BufferedReader br = new BufferedReader(new FileReader(file))){
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                rawText = sb.toString();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

            // Turn the raw text to JSON
            JSONObject storyJSON = new JSONObject(rawText);
            Story story = Story.fromJSON(storyJSON);

            // Add the story to the story dictionary
            storyDict.put(story.getName(), story);
        }
    }

    /**
     * Saves a story to the file.
     * @param story story to be saved
     */
    public void saveStory(Story story) {
        storyDict.put(story.getName(), story);

        // Save to file
        try {
            File file = new File(Config.PATH_TO_SAVE_FILE + "/" + story.getName() + ".json");

            // If file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // Create File/BufferedWriter to write to file
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            // Print to file
            JSONObject storyJSON = story.toJSON();
            bw.write(storyJSON.toString(4));

            // Close BufferedWriter
            bw.close();

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Loads a story.
     * @param storyName name of the story
     * @return a Story object with the same name if it exists. Otherwise, return null.
     */
    public Story loadStory(String storyName) {
        return storyDict.get(storyName);
    }

    /**
     * Returns a list of names of all the saved stories.
     * @return list of strings
     */
    public List<String> getStoryNames() {
        // Get the key set from the story hashtable and turn that into a sorted list
        Set<String> storyNameSet = storyDict.keySet();
        List<String> storyNameList = new ArrayList<>(storyNameSet);
        Collections.sort(storyNameList);

        return storyNameList;
    }
}
