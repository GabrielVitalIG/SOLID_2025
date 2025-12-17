package client;

/**
 * Validates user input on the client side before sending it to the server.
 * This prevents sending garbage requests that we know will fail.
 */
public class ClientCommandParser {

    /**
     * Checks if the command string is well-formed.
     * @param input The raw input line from the user.
     * @return true if valid, false otherwise.
     */
    public static boolean validate(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        // Split by space to get the command keyword
        // (This is a simple split; dealing with quotes properly happens on the server)
        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toUpperCase();

        switch (command) {
            case "ADD_MOVIE":
                // Needs at least: ADD_MOVIE <Title> <Genre>
                if (parts.length < 3) {
                    System.out.println("Local Error: ADD_MOVIE requires a Title and at least one Genre.");
                    return false;
                }
                return true;

            case "LIST_SUBTREE":
                // Needs at least: LIST_SUBTREE <Genre/Root>
                if (parts.length < 2) {
                    System.out.println("Local Error: LIST_SUBTREE requires a Genre name or 'Root'.");
                    return false;
                }
                return true;

            case "RATE_MOVIE":
                // Needs: RATE_MOVIE <Title> <Score>
                if (parts.length < 3) {
                    System.out.println("Local Error: RATE_MOVIE requires a Title and a Rating (0-10).");
                    return false;
                }
                // Optional: Check if the last part is a number
                try {
                    Double.parseDouble(parts[parts.length - 1]);
                } catch (NumberFormatException e) {
                    System.out.println("Local Error: Rating must be a number.");
                    return false;
                }
                return true;

            case "RECOMMEND":
                return true; // No args needed

            case "HELP":
            case "QUIT":
            case "LOGIN":
                return true;

            default:
                System.out.println("Local Error: Unknown command '" + command + "'. Type HELP for list.");
                return false;
        }
    }
}