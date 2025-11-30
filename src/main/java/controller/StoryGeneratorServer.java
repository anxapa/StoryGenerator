package controller;

import config.Config;
import service.GeminiAPIService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoryGeneratorServer {
    private final int port;
    private final GeminiAPIService geminiAPI;
    private final ExecutorService threadPool;
    private volatile boolean running;

    public StoryGeneratorServer(int port) {
        this.port = port;
        this.geminiAPI = new GeminiAPIService();
        this.threadPool = Executors.newFixedThreadPool(10);
        this.running = false;
    }

    /**
     * Starts the server.
     * @throws IOException
     */
    public void start() throws IOException {
        // Authenticate with Gemini API
        System.out.println("Authenticating with Gemini API...");
        if (geminiAPI.authenticate()) {
            System.out.println("Successfully authenticated with Gemini API");
        }
        else {
            System.err.println("Error in authenticating with Gemini API");
            shutdown();
        }

        running = true;

        // Create server socket and listen for client connections
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Story Generation Server started on port " + port);
            System.out.println("Waiting for client connection...");

            while (running) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();

                // Handle client in separate thread from pool
                ClientHandler handler = new ClientHandler(clientSocket, geminiAPI);
                threadPool.execute(handler);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            throw e;
        } finally {
            shutdown();
        }
    }

    /**
     * Turns off the server.
     */
    public void shutdown() {
        running = false;
        threadPool.shutdown();

        // TODO: Close APIs here
        System.out.println("Server shutdown complete");
    }

    /**
     * Checks if the server can run.
     * @return true if the server can run, otherwise false.
     */
    public boolean testRun() throws IOException {
        boolean canRun;

        // Authenticate with Gemini API
        System.out.println("Authenticating with Gemini API...");
        if (geminiAPI.authenticate()) {
            System.out.println("Successfully authenticated with Gemini API");
        }
        else {
            System.err.println("Error in authenticating with Gemini API");
            shutdown();
        }

        running = true;

        // Create server socket and listen for client connections
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Story Generation Server started on port " + port);
            canRun = true;
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            canRun = false;
        } finally {
            shutdown();
        }

        return canRun;
    }

    /**
     * Checks if the server is currently running.
     * @return boolean
     */
    public boolean isRunning() {
        return running;
    }

    public static void main(String[] args) {
        StoryGeneratorServer server = new StoryGeneratorServer(Config.SERVER_PORT);

        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
