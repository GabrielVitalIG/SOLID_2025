package server.commands;

import model.GenreTree;
import model.Movie;
import server.ClientHandler;
import strategy.RecommendationStrategy;
import strategy.TopRatedStrategy;

import java.util.List;

public class RecommendCommand implements Command {

    @Override
    public void execute(String[] args, GenreTree tree, ClientHandler client) {
        // 1. Select the strategy
        // In the future, 'args[0]' could specify the strategy type (e.g. "TOP" or "SIMILAR")
        // For now, we default to TopRated.
        RecommendationStrategy strategy = new TopRatedStrategy();

        // 2. Execute the strategy
        // We pass "User1" as a dummy ID for now since we haven't implemented User profiles yet.
        List<Movie> recommendations = strategy.recommend(tree, "User1");

        // 3. Send results to client
        if (recommendations.isEmpty()) {
            client.sendMessage("No movies found to recommend.");
        } else {
            client.sendMessage("--- Top Recommended Movies ---");
            for (Movie m : recommendations) {
                client.sendMessage(String.format("%s (Rating: %.1f)", m.getName(), m.getAverageRating()));
            }
        }
    }
}