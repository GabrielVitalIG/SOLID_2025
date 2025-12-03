package model;

import java.util.ArrayList;
import java.util.List;

public class Movie extends GenreNode {
    private List<Double> ratings;

    public Movie(String title) {
        super(title); // Passes title as the "name" of the node
        this.ratings = new ArrayList<>();
    }

    // Movies cannot have children, so we override this to prevent it.
    @Override
    public void addChild(GenreNode node) {
        throw new UnsupportedOperationException("Cannot add children to a Movie.");
    }

    public void addRating(double rating) {
        if (rating >= 0 && rating <= 10) {
            ratings.add(rating);
        }
    }

    public double getAverageRating() {
        if (ratings.isEmpty()) return 0.0;
        double sum = 0;
        for (Double r : ratings) sum += r;
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