package server.commands;

import model.GenreTree;
import server.ClientHandler;

public interface Command {
    /**
     * Executes the specific logic for this command.
     * * @param args   The arguments provided by the client (excluding the command name).
     * @param tree   The shared GenreTree data structure.
     * @param client The client handler to send responses back to.
     */
    void execute(String[] args, GenreTree tree, ClientHandler client);
}