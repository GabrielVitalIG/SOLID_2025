package strategy;

import model.GenreNode;
import model.GenreTree;
import model.Movie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenreSimilarityStrategy implements RecommendationStrategy {

    // Threshold to consider a movie "liked" by the user
    private static final double LIKE_THRESHOLD = 7.0;

    @Override
    public List<Movie> recommend(GenreTree tree, String userId) {
        Set<String> likedGenres = new HashSet<>();
        Set<Movie> seenMovies = new HashSet<>();
        List<Movie> recommendations = new ArrayList<>();

        // Pass 1: Find out which genres the user likes
        analyzeUserPreferences(tree.getRoot(), userId, "Root", likedGenres, seenMovies);

        // Pass 2: Find unseen movies in those genres
        findSimilarMovies(tree.getRoot(), "Root", likedGenres, seenMovies, recommendations);

        return recommendations;
    }

    /**
     * Recursive helper to find genres of movies the user rated highly.
     */
    private void analyzeUserPreferences(GenreNode node, String userId, String currentGenre,
                                        Set<String> likedGenres, Set<Movie> seenMovies) {

        if (node instanceof Movie) {
            Movie movie = (Movie) node;
            double userRating = movie.getUserRating(userId);

            // If user rated this movie
            if (userRating != -1) {
                seenMovies.add(movie);
                // If they liked it, remember this genre!
                if (userRating >= LIKE_THRESHOLD) {
                    likedGenres.add(currentGenre);
                }
            }
        } else {
            // It's a GenreNode
            for (GenreNode child : node.getChildren()) {
                // Pass the current node's name down as the "Genre Name" for the child
                analyzeUserPreferences(child, userId, node.getName(), likedGenres, seenMovies);
            }
        }
    }

    /**
     * Recursive helper to collect movies from the liked genres.
     */
    private void findSimilarMovies(GenreNode node, String currentGenre,
                                   Set<String> likedGenres, Set<Movie> seenMovies,
                                   List<Movie> accumulator) {

        if (node instanceof Movie) {
            Movie movie = (Movie) node;
            // Recommend if:
            // 1. We haven't seen it
            // 2. It belongs to a genre we like
            if (!seenMovies.contains(movie) && likedGenres.contains(currentGenre)) {
                accumulator.add(movie);
            }
        } else {
            // It's a GenreNode
            for (GenreNode child : node.getChildren()) {
                findSimilarMovies(child, node.getName(), likedGenres, seenMovies, accumulator);
            }
        }
    }
}