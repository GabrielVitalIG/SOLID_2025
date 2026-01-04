package model;

public class Rating {
    private String user;
    private double score;

    public Rating(String user, double score) {
        this.user = user;
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public String getUser() {
        return user;
    }
}