package model;

import java.util.ArrayList;
import java.util.List;

public class Movie extends GenreNode {
    private List<Rating> ratings;

    public Movie(String title) {
        super(title);
        this.ratings = new ArrayList<>();
    }

    @Override
    public void addChild(GenreNode node) {
        throw new UnsupportedOperationException("Cannot add children to a Movie.");
    }

    public void addRating(String userId, double score) {
        if (score >= 0 && score <= 10) {
            ratings.add(new Rating(userId, score));
        }
    }

    public void addRating(double score) {
        addRating("Anonymous", score);
    }

    public double getUserRating(String userId) {
        for (Rating r : ratings) {
            if (r.getUser().equalsIgnoreCase(userId)) {
                return r.getScore();
            }
        }
        return -1;
    }

    public double getAverageRating() {
        if (ratings.isEmpty()) return 0.0;
        double sum = 0;
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