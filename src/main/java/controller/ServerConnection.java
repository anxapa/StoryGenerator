package controller;

import config.Config;
import model.Story;
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
        socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Connected to a server.");
    }

    // TODO: Add expected methods here
    public String createStory(String prompt) throws IOException {
        JSONObject request = new JSONObject();
        request.put("action", "CREATE_STORY");
        request.put("prompt", prompt);

        return sendRequest(request);
    }

    public JSONObject extractJSONfromStory(String storyText) throws IOException{
        JSONObject request = new JSONObject();
        request.put("action", "EXTRACT_STORY");
        request.put("prompt", storyText);

        String response = sendRequest(request);
        return new JSONObject(response);
    }

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

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
