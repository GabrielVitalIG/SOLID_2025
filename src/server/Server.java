package server;

import model.GenreTree;
import persistence.JsonPersistence;
import persistence.TreePersistence;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 8888;
    // The file where data will be saved
    private static final String DB_FILE = "recomtree_data.json";

    private static GenreTree genreTree;
    // The persistence handler
    private static TreePersistence persistence;

    public static void main(String[] args) {
        // 1. Initialize Persistence Logic
        persistence = new JsonPersistence(DB_FILE);

        // 2. Try to load existing data from the JSON file
        System.out.println("Loading data from " + DB_FILE + "...");
        try {
            genreTree = persistence.load();
        } catch (IOException e) {
            System.err.println("Could not load data (first run?): " + e.getMessage());
        }

        // 3. If no data exists (or file is missing), create a fresh tree with dummy data
        if (genreTree == null) {
            System.out.println("No existing data found. Creating new tree.");
            genreTree = new GenreTree();
            initializeDummyData();
            saveData(); // Save immediately so the file is created
        }

        // 4. Add a "Shutdown Hook" to ensure data is saved when you stop the server (Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nServer stopping... Saving data.");
            saveData();
        }));

        System.out.println("RecomTree Server starting on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Accept connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Create handler
                ClientHandler handler = new ClientHandler(clientSocket, genreTree);

                // Start thread
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to save the tree to the JSON file.
     * You can call this from your Commands (e.g. AddMovieCommand) if you want real-time saving.
     */
    public static void saveData() {
        if (genreTree == null || persistence == null) return;

        try {
            persistence.save(genreTree);
            System.out.println("Data saved to " + DB_FILE);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private static void initializeDummyData() {
        genreTree.addMovie("Inception", "Action", "Sci-Fi");
        genreTree.addMovie("The Matrix", "Action", "Sci-Fi");
        genreTree.addMovie("Toy Story", "Animation", "Family");
        System.out.println("Dummy data initialized.");
    }
}