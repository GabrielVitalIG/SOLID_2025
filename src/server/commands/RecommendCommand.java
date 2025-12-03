package server.commands;

import model.GenreTree;
import model.Movie;
import server.ClientHandler;
import strategy.GenreSimilarityStrategy; // Import the new strategy
import strategy.RecommendationStrategy;
import strategy.TopRatedStrategy;

import java.util.List;

public class RecommendCommand implements Command {

    @Override
    public void execute(String[] args, GenreTree tree, ClientHandler client) {
        RecommendationStrategy strategy;
        String headerMessage;

        // 1. Select the strategy based on user input
        // Check if the first argument exists and is "SIMILAR"
        if (args.length > 0 && "SIMILAR".equalsIgnoreCase(args[0])) {
            strategy = new GenreSimilarityStrategy();
            headerMessage = "--- Recommended Based on Your Favorites ---";
        } else {
            // Default behavior if no argument or unknown argument is provided
            strategy = new TopRatedStrategy();
            headerMessage = "--- Top Recommended Movies ---";
        }

        // 2. Execute the strategy
        // We pass "User1" as a dummy ID. In a full system, this would come from the client's login session.
        String currentUserId = "User1";
        List<Movie> recommendations = strategy.recommend(tree, currentUserId);

        // 3. Send results to client
        if (recommendations.isEmpty()) {
            client.sendMessage("No movies found to recommend.");
            if (strategy instanceof GenreSimilarityStrategy) {
                client.sendMessage("(Hint: Try rating some movies with high scores first!)");
            }
        } else {
            client.sendMessage(headerMessage);
            for (Movie m : recommendations) {
                client.sendMessage(String.format("%s (Avg Rating: %.1f)", m.getName(), m.getAverageRating()));
            }
        }
    }
}