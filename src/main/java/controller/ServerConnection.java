package controller;

import config.Config;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connect() throws IOException {
        socket = new Socket(Config.getServerHost(), Config.getServerPort());
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Connected to a server.");
    }

    /**
     * Requests the server to create a story with the given prompt and model.
     * @param prompt prompt for the story
     * @param model model to be used
     * @return text format of created story
     * @throws IOException
     */
    public String createStory(String prompt, String model) throws IOException {
        JSONObject request = new JSONObject();
        request.put("action", "CREATE_STORY");
        request.put("prompt", prompt);
        request.put("model", model);

        return sendRequest(request);
    }

    /**
     * Requests the JSON format of the given story.
     * @param storyText story to be extracted
     * @param model model to be used
     * @return JSON format of given story
     * @throws IOException
     */
    public String extractJSONfromStory(String storyText, String model) throws IOException{
        JSONObject request = new JSONObject();
        request.put("action", "EXTRACT_STORY");
        request.put("model", model);
        request.put("prompt", storyText);

        String response = sendRequest(request);
        return response;
    }

    /**
     * Send the request to the server.
     * @param request text format of the request
     * @return text format of the response
     * @throws IOException
     */
    public String sendRequest(JSONObject request) throws IOException {
        // Send request
        out.println(request.toString());

        // Receive response
        StringBuilder sb = new StringBuilder();
        String line;
        String response;

        while (!(line = in.readLine()).equals("END")) {
            sb.append(line).append("\n");
        }

        response = sb.toString();

        return response;
    }

    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error disconnecting from server: " + e.getMessage());
        }
    }

    /**
     * Checks if the connection is still connected to the server.
     * @return true if still connected to server, else false.
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
