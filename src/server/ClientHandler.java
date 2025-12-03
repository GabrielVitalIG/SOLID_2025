package server;

import model.GenreTree;
import server.commands.Command;
import server.commands.CommandFactory;
import utils.Logger; // Import Logger

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
            // Send the END marker for the welcome message too, just to be safe with the parser
            out.println("<END>");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Log the incoming command
                Logger.log("Client " + clientSocket.getPort() + " sent: " + inputLine);

                // Handle disconnect
                if ("QUIT".equalsIgnoreCase(inputLine.trim())) {
                    out.println("Goodbye!");
                    out.println("<END>");
                    break;
                }

                // 1. Identify the command
                Command cmd = CommandFactory.getCommand(inputLine);

                if (cmd != null) {
                    // 2. Parse arguments using the public helper
                    List<String> tokens = CommandFactory.parseInput(inputLine);

                    // Skip the first token (the command name) to get just arguments
                    String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

                    // 3. Execute
                    cmd.execute(args, genreTree, this);
                } else {
                    out.println("ERROR: Unknown command.");
                }

                // CRITICAL: Send the end-of-response marker so the client stops reading
                out.println("<END>");
            }

        } catch (IOException e) {
            Logger.error("Error handling client " + clientSocket.getPort() + ": " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Logger.log("Client " + clientSocket.getPort() + " disconnected.");
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }
}