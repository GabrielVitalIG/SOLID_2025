package server;

import model.GenreTree;
import server.commands.Command;
import server.commands.CommandFactory;
import utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private GenreTree genreTree;
    private PrintWriter out;
    private BufferedReader in;

    private String currentUserId = "Guest";
    private boolean isAdmin = false;

    public ClientHandler(Socket socket, GenreTree tree) {
        this.clientSocket = socket;
        this.genreTree = tree;
    }

    public String getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(String userId) { this.currentUserId = userId; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
    // --------------------------------------

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Updated Welcome Message
            out.println("Welcome to RecomTree! Login to track ratings.");
            out.println("Type 'LOGIN <username>' or 'LOGIN admin <password>'");
            out.println("<END>");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                Logger.log("[" + currentUserId + "] sent: " + inputLine);

                if ("QUIT".equalsIgnoreCase(inputLine.trim())) {
                    out.println("Goodbye, " + currentUserId + "!");
                    out.println("<END>");
                    break;
                }

                Command cmd = CommandFactory.getCommand(inputLine);

                if (cmd != null) {
                    List<String> tokens = CommandFactory.parseInput(inputLine);
                    String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

                    cmd.execute(args, genreTree, this);
                } else {
                    out.println("ERROR: Unknown command.");
                }

                out.println("<END>");
            }

        } catch (IOException e) {
            Logger.error("Error handling client " + clientSocket.getPort() + ": " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) { e.printStackTrace(); }
            Logger.log("Client " + clientSocket.getPort() + " disconnected.");
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }
}