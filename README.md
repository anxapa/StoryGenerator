# Story Generator

## Setup
1. Get API key from [Google AI Studio](https://aistudio.google.com/app/api-keys)
2. Create a `config.properties` file in `src/main/resources` using the template given in the same directory called `config.properties.template`:
```
GEMINI_API_KEY=<insert key here>
SERVER_PORT=8888
SERVER_HOST=localhost
PATH_TO_SAVE_FILE=src/main/resources/saves
```

3. Run `StoryGenerator.java` (for the server) and `StoryGeneratorGUI.java` (for the client)

## Features
- [x] Create stories of different types - narratives, screenplays, and poems
- [x] Extract features (such as characters and locations) from the story generated 
- [x] Different options for the story generated
- [x] Error handling 
- [x] Save/load sessions

## Design Patterns
- Strategy: Different writing modes (STILL IN-PROGRESS)
- Singleton: Access global, centralized save and load functionality within the program.
- Observer: Swing UI elements react whenever an action is done (for example, when a button is clicked).

## Demo
[Demo video](https://youtu.be/NjfyeM_EA68)
