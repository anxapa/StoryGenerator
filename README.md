# Story Generator

## Setup
1. Get API key from [Google AI Studio](https://aistudio.google.com/app/api-keys)
2. Create a config file in `src/main/java/config` using the template below:
```java
package config;

public class Config {
    // API-related
    public static final String GEMINI_API_KEY = "Insert GEMINI API KEY here";

    // Server-related
    public static final int SERVER_PORT = 8888;
    public static final String SERVER_HOST = "localhost";

    // File-related
    public static final String PATH_TO_SAVE_FILE = "src/main/resources/saves";
}
```
3. Run `StoryGenerator.java` (for the server) and `StoryGeneratorGUI.java` (for the client)

## Features
- [x] Extract features (such as characters and locations) from the story generated.
- [x] Save/load sessions

## Design Patterns
- Strategy: Different writing modes (STILL IN-PROGRESS)
- Singleton: Access global, centralized save and load functionality within the program.

## Demo
[Demo link](https://youtu.be/CoCq25YsJ5E)
