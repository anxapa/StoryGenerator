package controller;

import model.Story;
import org.json.JSONObject;
import service.GeminiAPIService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private final GeminiAPIService geminiAPI;

    public ClientHandler(Socket socket, GeminiAPIService geminiAPI) {
        this.clientSocket = socket;
        this.geminiAPI = geminiAPI;
    }

    @Override
    public void run() {
        System.out.println("New client connected: " + clientSocket.getInetAddress());

        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Received request: " + request);
                String response = handleRequest(request);
                out.println(response);
                System.out.println("Handled request: " + request);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected");
            } catch (IOException e) {
                System.err.println("Error disconnecting from client: " + e.getMessage());
            }
        }
    }

    private String handleRequest(String request) {
        JSONObject requestJSON = new JSONObject(request);
        String prompt = requestJSON.getString("prompt");
        switch (requestJSON.getString("action")) {
            case "CREATE_STORY" -> {
                return generateStory(prompt);
            }
            case "EXTRACT_STORY" -> {
                return extractStory(prompt);
            }
            default -> {
                return null;
            }
        }
    }

    private String generateStory(String request) {
        String message = geminiAPI.call(request);
        System.out.println(message);
        return message;
    }

    public String extractStory(String story) {
        return geminiAPI.extractStory(story);
    }
}
