package server.commands;

import model.GenreTree;
import model.Movie;
import server.ClientHandler;
import server.Server;

public class RateMovieCommand implements Command {

    @Override
    public void execute(String[] args, GenreTree tree, ClientHandler client) {
        if (args.length < 2) {
            client.sendMessage("ERROR: Usage: RATE_MOVIE <Title> <Rating (0-10)>");
            return;
        }

        String title = args[0];
        try {
            double rating = Double.parseDouble(args[1]);

            if (rating < 0 || rating > 10) {
                client.sendMessage("ERROR: Rating must be between 0 and 10.");
                return;
            }

            Movie movie = tree.findMovie(title);

            if (movie == null) {
                client.sendMessage("ERROR: Movie '" + title + "' not found.");
            } else {
                String currentUser = client.getCurrentUserId();
                movie.addRating(currentUser, rating);

                String newAvg = String.format("%.1f", movie.getAverageRating());
                client.sendMessage("SUCCESS: Rated '" + title + "' as " + currentUser + " - New Average: " + newAvg);

                Server.saveData();
            }

        } catch (NumberFormatException e) {
            client.sendMessage("ERROR: Rating must be a number.");
        }
    }
}