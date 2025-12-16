package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    // API-related
    private static String GEMINI_API_KEY;

    // Server-related
    private static int SERVER_PORT;
    private static String SERVER_HOST;

    // File-related
    private static String PATH_TO_SAVE_FILE;

    // Load from config.properties
    public static void load() {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("src/main/resources/config.properties")){
            prop.load(input);

            // Get the property value and print it out
            GEMINI_API_KEY = prop.getProperty("GEMINI_API_KEY");
            SERVER_HOST = prop.getProperty("SERVER_HOST");
            SERVER_PORT = Integer.parseInt(prop.getProperty("SERVER_PORT"));
            PATH_TO_SAVE_FILE = prop.getProperty("PATH_TO_SAVE_FILE");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getGeminiApiKey() {
        return GEMINI_API_KEY;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static String getPathToSaveFile() {
        return PATH_TO_SAVE_FILE;
    }
}