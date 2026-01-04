package persistence;

import com.google.gson.*;
import model.GenreNode;
import model.GenreTree;
import model.Movie;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class JsonPersistence implements TreePersistence {
    private String filePath;
    private Gson gson;

    public JsonPersistence(String filePath) {
        this.filePath = filePath;

        // Register the custom deserializer to handle Polymorphism (Movie vs GenreNode)
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(GenreNode.class, new GenreNodeDeserializer())
                .create();
    }

    @Override
    public void save(GenreTree tree) throws IOException {
        try (Writer writer = new FileWriter(filePath)) {
            // We only need to save the root; the children are saved recursively
            gson.toJson(tree.getRoot(), writer);
        }
    }

    @Override
    public GenreTree load() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        try (Reader reader = new FileReader(filePath)) {
            // Load the root node
            GenreNode root = gson.fromJson(reader, GenreNode.class);

            // Reconstruct the tree wrapper
            GenreTree tree = new GenreTree();
            tree.setRoot(root);
            return tree;
        }
    }

    /**
     * Custom Deserializer Logic:
     * How to tell the difference between a "GenreNode" and a "Movie" in JSON?
     * Heuristic: Movies have a "ratings" list. Genres do not.
     */
    private class GenreNodeDeserializer implements JsonDeserializer<GenreNode> {
        @Override
        public GenreNode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            JsonObject jsonObject = json.getAsJsonObject();

            // If the JSON object has "ratings", we treat it as a Movie
            if (jsonObject.has("ratings")) {
                // Deserialize as Movie
                return context.deserialize(json, Movie.class);
            } else {
                String name = jsonObject.get("name").getAsString();
                GenreNode node = new GenreNode(name);

                // Manually populate children if they exist
                if (jsonObject.has("children")) {
                    JsonArray childrenArray = jsonObject.getAsJsonArray("children");
                    for (JsonElement childElem : childrenArray) {
                        // Recursively call this deserializer for children
                        GenreNode child = deserialize(childElem, GenreNode.class, context);
                        node.addChild(child);
                    }
                }
                return node;
            }
        }
    }
}