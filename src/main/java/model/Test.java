package model;

import controller.GenerationEngine;
import controller.NarrativeStrategy;
import controller.SaveLoadHandler;
import controller.ServerConnection;
import org.json.JSONObject;
import org.json.JSONStringer;
import service.GeminiAPIService;

import java.io.IOException;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        GeminiAPIService api = new GeminiAPIService();
        System.out.println(api.authenticate());
    }

    private static void connectionTest() {
        GenerationEngine engine = new GenerationEngine(new NarrativeStrategy());
        ServerConnection serverConnection = new ServerConnection();
        engine.setQuality(1);

        try {
            serverConnection.connect();
            Scanner sc = new Scanner(System.in);
            while (sc.hasNext()) {
                String prompt = engine.construct(sc.nextLine());
                String response = serverConnection.createStory(prompt);

                // Extract story to JSON to Story Object
                JSONObject storyJSON = serverConnection.extractJSONfromStory(response);
                Story story = Story.fromJSON(storyJSON);

                story.setStory(response);
                System.out.println(response);
                SaveLoadHandler.getInstance().saveStory(story);
            }

        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            System.out.println(("Not connected"));
        }
    }

    private static void storyTest() {
        Story story = new Story("Stormlight Archive");
        story.setStory("Storms.");
        story.addCharacter(new StoryCharacter("Kaladin", 21, "Alethi", "Human", "Male", "Surgeon"));
        story.addCharacter(new StoryCharacter("Shallan", 17, "Veden", "Human", "Female", "Artist"));
        story.addLocation(new StoryLocation("Urithuru", "Tower"));
        story.addLocation(new StoryLocation("Shattered Plains", "Battlefield"));

        System.out.println(story.getLocation("Urithuru"));
        SaveLoadHandler.getInstance().saveStory(story);
    }

    private static void JSONExampleStringer() {
        //We initializate the JSONStringer

        JSONStringer jsonStringer = new JSONStringer();

        //Now we start the process of adding elements with .object()

        jsonStringer.object();

        //We can now add elements as keys and values with .values () and .key()

        jsonStringer.key("trueValue").value(true);
        jsonStringer.key("falseValue").value(false);
        jsonStringer.key("nullValue").value(null);
        jsonStringer.key("stringValue").value("hello world!");
        jsonStringer.key("complexStringValue").value("h\be\tllo w\u1234orld!");
        jsonStringer.key("intValue").value(42);
        jsonStringer.key("doubleValue").value(-23.45e67);

        //We end this prcedure with .ednObject

        jsonStringer.endObject();

        //Once we have a JSONStringer, we convert it to JSONObject generating a String and using JSONObject's contructor.

        String str = jsonStringer.toString();
        JSONObject jsonObject = new JSONObject(str);

        System.out.println("Final JSONOBject: " + jsonObject);
    }
}
