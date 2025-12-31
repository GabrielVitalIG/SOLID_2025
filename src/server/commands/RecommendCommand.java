package server.commands;

import model.GenreTree;
import model.Movie;
import server.ClientHandler;
import strategy.GenreSimilarityStrategy;
import strategy.RecommendationStrategy;
import strategy.TopRatedStrategy;

import java.util.List;

public class RecommendCommand implements Command {

    @Override
    public void execute(String[] args, GenreTree tree, ClientHandler client) {
        RecommendationStrategy strategy;
        String headerMessage;

        if (args.length > 0 && "SIMILAR".equalsIgnoreCase(args[0])) {
            strategy = new GenreSimilarityStrategy();
            headerMessage = "--- Recommended Based on Your Favorites ---";
        } else {
            strategy = new TopRatedStrategy();
            headerMessage = "--- Top Recommended Movies ---";
        }

        // UPDATE: Get actual ID
        String currentUserId = client.getCurrentUserId();
        List<Movie> recommendations = strategy.recommend(tree, currentUserId);

        if (recommendations.isEmpty()) {
            client.sendMessage("No movies found to recommend.");
            if (strategy instanceof GenreSimilarityStrategy) {
                client.sendMessage("(Hint: " + currentUserId + ", try rating some movies with high scores first!)");
            }
        } else {
            client.sendMessage(headerMessage);
            for (Movie m : recommendations) {
                client.sendMessage(String.format("%s (Avg Rating: %.1f)", m.getName(), m.getAverageRating()));
            }
        }
    }
}