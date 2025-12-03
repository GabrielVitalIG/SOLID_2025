package server.commands;

import model.GenreTree;
import model.Movie;
import server.ClientHandler;

public class RateMovieCommand implements Command {

    @Override
    public void execute(String[] args, GenreTree tree, ClientHandler client) {
        // Validation: Expecting something like "Inception" "9.5"
        if (args.length < 2) {
            client.sendMessage("ERROR: Usage: RATE_MOVIE <Title> <Rating (0-10)>");
            return;
        }

        String title = args[0];
        String ratingStr = args[1];

        try {
            double rating = Double.parseDouble(ratingStr);

            if (rating < 0 || rating > 10) {
                client.sendMessage("ERROR: Rating must be between 0 and 10.");
                return;
            }

            // Find the movie using the global search method in GenreTree
            Movie movie = tree.findMovie(title);

            if (movie == null) {
                client.sendMessage("ERROR: Movie '" + title + "' not found.");
            } else {
                movie.addRating(rating);
                // Calculate new average for feedback
                String newAvg = String.format("%.1f", movie.getAverageRating());
                client.sendMessage("SUCCESS: Rated '" + title + "' - New Average: " + newAvg);
            }

        } catch (NumberFormatException e) {
            client.sendMessage("ERROR: Rating must be a number.");
        }
    }
}