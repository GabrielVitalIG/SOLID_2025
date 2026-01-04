package server;

import model.GenreTree;
import persistence.JsonPersistence;
import persistence.TreePersistence;
import utils.Logger;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 5555;

    private static final String DB_FILE = "recomtree_data.json";
    private static GenreTree genreTree;
    private static TreePersistence persistence;

    public static void main(String[] args) {
        persistence = new JsonPersistence(DB_FILE);

        Logger.log("Loading data from " + DB_FILE + "...");
        try {
            genreTree = persistence.load();
        } catch (IOException e) {
            Logger.error("Could not load data (first run?): " + e.getMessage());
        }

        if (genreTree == null) {
            Logger.log("No existing data found. Creating new tree.");
            genreTree = new GenreTree();
            initializeDummyData();
            saveData();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.log("Server stopping... Saving data.");
            saveData();
        }));

        Logger.log("RecomTree Server starting on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Logger.log("New client connected: " + clientSocket.getInetAddress());
                ClientHandler handler = new ClientHandler(clientSocket, genreTree);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            Logger.error("Server exception: " + e.getMessage());
        }
    }

    public static void saveData() {
        if (genreTree == null || persistence == null) return;
        try {
            persistence.save(genreTree);
            Logger.log("Data saved to " + DB_FILE);
        } catch (IOException e) {
            Logger.error("Error saving data: " + e.getMessage());
        }
    }

    private static void initializeDummyData() {
        genreTree.addMovie("Inception", "Action", "Sci-Fi");
        genreTree.addMovie("The Matrix", "Action", "Sci-Fi");
        genreTree.addMovie("Toy Story", "Animation", "Family");
        Logger.log("Dummy data initialized.");
    }
}