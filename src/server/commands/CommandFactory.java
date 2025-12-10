package server.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandFactory {

    public static Command getCommand(String inputLine) {
        if (inputLine == null || inputLine.trim().isEmpty()) {
            return null;
        }

        // 1. Parse the input string into a list of arguments (respecting quotes)
        List<String> tokens = parseInput(inputLine);
        if (tokens.isEmpty()) return null;

        // The first token is the Command Keyword (e.g., "ADD_MOVIE")
        String keyword = tokens.get(0).toUpperCase();

        // Remove the keyword from the args list to pass only parameters to the command
        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

        // 2. Return the correct Command object based on the keyword
        switch (keyword) {
            case "ADD_MOVIE":
                return new AddMovieCommand();

            case "LIST_SUBTREE":
                return new ListSubtreeCommand();

            case "RECOMMEND":
                return new RecommendCommand();

            case "RATE_MOVIE":
                return new RateMovieCommand();

            case "HELP":
                return new HelpCommand();

            default:
                return null; // Command not found
        }
    }

    /**
     * Helper to split string by spaces but keep quoted text together.
     * Example: 'ADD_MOVIE "Star Wars" Sci-Fi' -> ["ADD_MOVIE", "Star Wars", "Sci-Fi"]
     */
    public static List<String> parseInput(String input) {
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
        while (m.find()) {
            // CLEANUP: Replace quotes AND trim whitespace
            String cleanPart = m.group(1).replace("\"", "").trim();
            if (!cleanPart.isEmpty()) {
                list.add(cleanPart);
            }
        }
        return list;
    }
}