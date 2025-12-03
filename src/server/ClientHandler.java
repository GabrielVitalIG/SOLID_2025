package server;

import model.GenreTree;
import server.commands.Command;
import server.commands.CommandFactory;

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

    public ClientHandler(Socket socket, GenreTree tree) {
        this.clientSocket = socket;
        this.genreTree = tree;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Welcome to RecomTree! Type 'help' for commands.");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Handle disconnect
                if ("QUIT".equalsIgnoreCase(inputLine.trim())) {
                    out.println("Goodbye!");
                    break;
                }

                // 1. Identify the command
                Command cmd = CommandFactory.getCommand(inputLine);

                if (cmd != null) {
                    // 2. Parse arguments using the public helper
                    // Make sure you changed parseInput to 'public' in CommandFactory!
                    List<String> tokens = CommandFactory.parseInput(inputLine);

                    // Skip the first token (the command name) to get just arguments
                    String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

                    // 3. Execute
                    cmd.execute(args, genreTree, this);
                } else {
                    out.println("ERROR: Unknown command.");
                }
            }

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }
}