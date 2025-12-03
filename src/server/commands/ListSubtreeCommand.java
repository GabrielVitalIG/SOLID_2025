package server.commands;

import model.GenreNode;
import model.GenreTree;
import model.Movie;
import server.ClientHandler;

public class ListSubtreeCommand implements Command {

    @Override
    public void execute(String[] args, GenreTree tree, ClientHandler client) {
        // 1. Determine where to start listing (Root by default, or specific path)
        GenreNode current = tree.getRoot();

        // Navigate down the path provided in args (e.g. "Action" "Sci-Fi")
        for (String genreName : args) {
            current = current.getChild(genreName);
            if (current == null) {
                client.sendMessage("ERROR: Genre not found: " + genreName);
                return;
            }
        }

        // 2. Build the string representation recursively
        StringBuilder sb = new StringBuilder();
        sb.append("Listing contents for: ").append(current.getName()).append("\n");
        buildSubtreeString(current, 0, sb);

        // 3. Send the final string to the client
        client.sendMessage(sb.toString());
    }

    /**
     * Recursive helper to build a string of the tree structure.
     * This replaces the "display()" method in the model for network use.
     */
    private void buildSubtreeString(GenreNode node, int indentLevel, StringBuilder sb) {
        String indent = "  ".repeat(indentLevel);

        if (node instanceof Movie) {
            Movie m = (Movie) node;
            sb.append(indent)
                    .append("- ")
                    .append(m.getName())
                    .append(" (Avg: ")
                    .append(String.format("%.1f", m.getAverageRating()))
                    .append(")\n");
        } else {
            // It's a Genre
            // Only print the name if it's NOT the top-level node we are listing
            // (Optional cosmetic choice, but usually looks cleaner)
            if (indentLevel > 0) {
                sb.append(indent).append("[").append(node.getName()).append("]\n");
            }

            for (GenreNode child : node.getChildren()) {
                buildSubtreeString(child, indentLevel + 1, sb);
            }
        }
    }
}