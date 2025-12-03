package server.commands;

import model.GenreTree;
import server.ClientHandler;
import java.util.Arrays;

public class AddMovieCommand implements Command {

    @Override
    public void execute(String[] args, GenreTree tree, ClientHandler client) {
        // Validation: We need at least a title and one genre
        if (args.length < 2) {
            client.sendMessage("ERROR: Usage: ADD_MOVIE <Title> <Genre1> [Genre2] ...");
            return;
        }

        String title = args[0];
        // The rest of the array (from index 1 to end) are the genres
        String[] genres = Arrays.copyOfRange(args, 1, args.length);

        try {
            // Update the model
            tree.addMovie(title, genres);

            // Confirm to the client
            client.sendMessage("SUCCESS: Added movie '" + title + "' to " + Arrays.toString(genres));

        } catch (IllegalArgumentException e) {
            client.sendMessage("ERROR: " + e.getMessage());
        } catch (Exception e) {
            client.sendMessage("ERROR: Could not add movie. " + e.getMessage());
        }
    }
}