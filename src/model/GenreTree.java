package model;

public class GenreTree {
    private GenreNode root;

    public GenreTree() {
        // The root is a special genre that holds all other genres
        this.root = new GenreNode("Root");
    }

    public GenreNode getRoot() {
        return root;
    }

    /**
     * Adds a movie to a specific genre path.
     * Example: addMovie("Inception", "Action", "Sci-Fi")
     * If the genres don't exist, it creates them.
     */
    public synchronized void addMovie(String title, String... genrePath) {
        GenreNode current = root;

        // 1. Traverse (or create) the genre path
        for (String genreName : genrePath) {
            GenreNode next = current.getChild(genreName);
            if (next == null) {
                // Create the genre if it doesn't exist
                next = new GenreNode(genreName);
                current.addChild(next);
            }
            // Ensure we aren't trying to traverse through a Movie
            if (next instanceof Movie) {
                throw new IllegalArgumentException("Invalid path: " + genreName + " is a movie, not a genre.");
            }
            current = next;
        }

        // 2. Add the movie to the final genre found
        // Check if movie already exists to avoid duplicates
        if (current.getChild(title) == null) {
            current.addChild(new Movie(title));
            System.out.println("Movie added: " + title);
        } else {
            System.out.println("Movie already exists: " + title);
        }
    }

    /**
     * Searches for a movie by title globally in the tree.
     * Returns the Movie object or null if not found.
     */
    public Movie findMovie(String title) {
        return findMovieRecursive(root, title);
    }

    private Movie findMovieRecursive(GenreNode node, String title) {
        // If this node IS the movie, return it
        if (node instanceof Movie && node.getName().equalsIgnoreCase(title)) {
            return (Movie) node;
        }

        // If it's a genre, look through its children
        for (GenreNode child : node.getChildren()) {
            Movie result = findMovieRecursive(child, title);
            if (result != null) return result;
        }
        return null;
    }

    // Add this to model/GenreTree.java
    public void setRoot(GenreNode root) {
        this.root = root;
    }
}