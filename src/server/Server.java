package server;

import model.GenreTree;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 8888;
    private static GenreTree genreTree;

    public static void main(String[] args) {
        genreTree = new GenreTree();
        initializeDummyData();

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

    private static void initializeDummyData() {
        genreTree.addMovie("Inception", "Action", "Sci-Fi");
        genreTree.addMovie("The Matrix", "Action", "Sci-Fi");
        genreTree.addMovie("Toy Story", "Animation", "Family");
        System.out.println("Dummy data initialized.");
    }
}