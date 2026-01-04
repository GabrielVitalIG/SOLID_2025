package server.commands;

import model.GenreTree;
import server.ClientHandler;
import server.Server; // Import to save data
import java.util.Arrays;

public class AddMovieCommand implements Command {

    @Override
    public void execute(String[] args, GenreTree tree, ClientHandler client) {
        if (!client.isAdmin()) {
            client.sendMessage("ERROR: Permission Denied. Only Admins can add movies.");
            return;
        }
        if (args.length < 2) {
            client.sendMessage("ERROR: Usage: ADD_MOVIE <Title> <Genre1> [Genre2] ...");
            return;
        }

        String title = args[0];
        String[] genres = Arrays.copyOfRange(args, 1, args.length);

        try {
            tree.addMovie(title, genres);
            client.sendMessage("SUCCESS: Added movie '" + title + "' to " + Arrays.toString(genres));

            // Save immediately
            Server.saveData();

        } catch (IllegalArgumentException e) {
            client.sendMessage("ERROR: " + e.getMessage());
        } catch (Exception e) {
            client.sendMessage("ERROR: Could not add movie. " + e.getMessage());
        }
    }
}