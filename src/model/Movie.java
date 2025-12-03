package model;

import java.util.ArrayList;
import java.util.List;

public class Movie extends GenreNode {
    // FIX 1: Store Rating objects, not Doubles
    private List<Rating> ratings;

    public Movie(String title) {
        super(title);
        this.ratings = new ArrayList<>();
    }

    @Override
    public void addChild(GenreNode node) {
        throw new UnsupportedOperationException("Cannot add children to a Movie.");
    }

    // FIX 2: addRating must now take a user ID + score
    // (We default to "User1" if you just want to support simple adds,
    // or you can change the signature in RateMovieCommand)
    public void addRating(String userId, double score) {
        if (score >= 0 && score <= 10) {
            ratings.add(new Rating(userId, score));
        }
    }

    // Overloaded method for backward compatibility (optional but helpful)
    // If we don't know the user, we just assign it to "Anonymous"
    public void addRating(double score) {
        addRating("Anonymous", score);
    }

    public double getUserRating(String userId) {
        // FIX 3: Iterate over Rating objects
        for (Rating r : ratings) {
            if (r.getUser().equalsIgnoreCase(userId)) {
                return r.getScore();
            }
        }
        return -1; // Not rated by this user
    }

    public double getAverageRating() {
        if (ratings.isEmpty()) return 0.0;
        double sum = 0;
        // FIX 4: Iterate over Rating objects to get the score
        for (Rating r : ratings) {
            sum += r.getScore();
        }
        return sum / ratings.size();
    }

    public int getRatingCount() {
        return ratings.size();
    }

    @Override
    public void display(int indentLevel) {
        String indent = " ".repeat(indentLevel * 2);
        System.out.println(indent + "- " + name + " (Avg: " + String.format("%.1f", getAverageRating()) + ")");
    }
}