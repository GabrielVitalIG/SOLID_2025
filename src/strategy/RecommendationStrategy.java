package strategy;

import model.GenreTree;
import model.Movie;
import java.util.List;

public interface RecommendationStrategy {
    /**
     * Generates a list of recommended movies based on the strategy logic.
     * @param tree The full movie database (GenreTree).
     * @param userId The ID of the user requesting recommendations (useful for history-based logic).
     * @return A list of Movie objects ordered by the recommendation criteria.
     */
    List<Movie> recommend(GenreTree tree, String userId);
}