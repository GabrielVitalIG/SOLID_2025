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
        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toUpperCase();

        switch (command) {
            case "ADD_MOVIE":
                if (parts.length < 3) {
                    System.out.println("Local Error: ADD_MOVIE requires a Title and at least one Genre.");
                    return false;
                }
                return true;

            case "LIST_SUBTREE":
                if (parts.length < 2) {
                    System.out.println("Local Error: LIST_SUBTREE requires a Genre name or 'Root'.");
                    return false;
                }
                return true;

            case "RATE_MOVIE":
                if (parts.length < 3) {
                    System.out.println("Local Error: RATE_MOVIE requires a Title and a Rating (0-10).");
                    return false;
                }

                try {
                    Double.parseDouble(parts[parts.length - 1]);
                } catch (NumberFormatException e) {
                    System.out.println("Local Error: Rating must be a number.");
                    return false;
                }
                return true;

            case "RECOMMEND":
                return true;

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