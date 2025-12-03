package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the tree.
 * By default, this acts as a "Genre" (Composite) that can hold children.
 */
public class GenreNode {
    protected String name;
    // We store children here. A child can be a sub-genre (GenreNode) or a Movie (which extends GenreNode).
    protected List<GenreNode> children;

    public GenreNode(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    /**
     * Adds a child node (sub-genre or movie) to this genre.
     */
    public void addChild(GenreNode node) {
        children.add(node);
    }

    public List<GenreNode> getChildren() {
        return children;
    }

    /**
     * Helper to find a direct child by name (e.g., finding "Sci-Fi" inside "Action").
     */
    public GenreNode getChild(String name) {
        for (GenreNode node : children) {
            if (node.getName().equalsIgnoreCase(name)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Recursive display method for the tree structure.
     */
    public void display(int indentLevel) {
        String indent = " ".repeat(indentLevel * 2);
        System.out.println(indent + "[" + name + "]");

        for (GenreNode child : children) {
            child.display(indentLevel + 1);
        }
    }
}