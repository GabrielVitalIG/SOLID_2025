package strategy;

import model.GenreNode;
import model.GenreTree;
import model.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TopRatedStrategy implements RecommendationStrategy {

    private static final int LIMIT = 5; // How many movies to recommend

    @Override
    public List<Movie> recommend(GenreTree tree, String userId) {
        // 1. Collect ALL movies from the tree
        List<Movie> allMovies = new ArrayList<>();
        collectMoviesRecursive(tree.getRoot(), allMovies);

        // 2. Sort them by average rating (Highest to Lowest)
        Collections.sort(allMovies, new Comparator<Movie>() {
            @Override
            public int compare(Movie m1, Movie m2) {
                // Compare doubles (m2 - m1 for descending order)
                return Double.compare(m2.getAverageRating(), m1.getAverageRating());
            }
        });

        // 3. Return the top N movies
        if (allMovies.size() > LIMIT) {
            return allMovies.subList(0, LIMIT);
        }
        return allMovies;
    }

    // Helper to traverse the Composite structure and find all Leaf nodes (Movies)
    private void collectMoviesRecursive(GenreNode node, List<Movie> accumulator) {
        if (node instanceof Movie) {
            accumulator.add((Movie) node);
        } else {
            // It's a genre, so recurse into children
            for (GenreNode child : node.getChildren()) {
                collectMoviesRecursive(child, accumulator);
            }
        }
    }
}