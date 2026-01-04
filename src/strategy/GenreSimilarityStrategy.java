package strategy;

import model.GenreNode;
import model.GenreTree;
import model.Movie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenreSimilarityStrategy implements RecommendationStrategy {

    private static final double LIKE_THRESHOLD = 7.0;

    @Override
    public List<Movie> recommend(GenreTree tree, String userId) {
        Set<String> likedGenres = new HashSet<>();
        Set<Movie> seenMovies = new HashSet<>();
        List<Movie> recommendations = new ArrayList<>();

        // Find ALL genres in the path of movies the user likes
        analyzeUserPreferences(tree.getRoot(), userId, new ArrayList<>(), likedGenres, seenMovies);

        // Traverse tree. If we hit a liked genre, recommend EVERYTHING below it.
        findSimilarMovies(tree.getRoot(), false, likedGenres, seenMovies, recommendations);

        return recommendations;
    }

    private void analyzeUserPreferences(GenreNode node, String userId, List<String> pathSoFar,
                                        Set<String> likedGenres, Set<Movie> seenMovies) {

        if (node instanceof Movie) {
            Movie movie = (Movie) node;
            double rating = movie.getUserRating(userId);
            if (rating != -1) {
                seenMovies.add(movie);
                if (rating >= LIKE_THRESHOLD) {
                    // USER LIKES THIS MOVIE -> Add ALL ancestors to likedGenres
                    likedGenres.addAll(pathSoFar);
                }
            }
        } else {
            // It's a Genre.
            List<String> childPath = new ArrayList<>(pathSoFar);
            if (!node.getName().equalsIgnoreCase("Root")) {
                childPath.add(node.getName());
            }
            for (GenreNode child : node.getChildren()) {
                analyzeUserPreferences(child, userId, childPath, likedGenres, seenMovies);
            }
        }
    }

    /**
     * Recursive helper to find movies.
     * @param insideLikedBranch true if one of the ancestors was a "Liked Genre"
     */
    private void findSimilarMovies(GenreNode node, boolean insideLikedBranch,
                                   Set<String> likedGenres, Set<Movie> seenMovies,
                                   List<Movie> accumulator) {

        // 1. Check if THIS node is a Liked Genre
        // If we are already in a liked branch, we stay true.
        // If not, we check if this specific node's name is in our liked list.
        boolean currentlyInteresting = insideLikedBranch || likedGenres.contains(node.getName());

        if (node instanceof Movie) {
            Movie movie = (Movie) node;
            // Recommend if:
            // 1. It's in an interesting branch (or direct parent is liked)
            // 2. We haven't seen it yet
            if (currentlyInteresting && !seenMovies.contains(movie)) {
                accumulator.add(movie);
            }
        } else {
            for (GenreNode child : node.getChildren()) {
                findSimilarMovies(child, currentlyInteresting, likedGenres, seenMovies, accumulator);
            }
        }
    }
}