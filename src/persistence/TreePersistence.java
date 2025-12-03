package persistence;

import model.GenreTree;
import java.io.IOException;

public interface TreePersistence {
    /**
     * Saves the current state of the GenreTree to a file.
     * @param tree The tree to save.
     * @throws IOException If writing fails.
     */
    void save(GenreTree tree) throws IOException;

    /**
     * Loads the GenreTree from a file.
     * @return The loaded GenreTree, or null if no file exists.
     * @throws IOException If reading fails.
     */
    GenreTree load() throws IOException;
}