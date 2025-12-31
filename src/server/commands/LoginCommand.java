package server.commands;

import model.GenreTree;
import server.ClientHandler;

public class LoginCommand implements Command {

    // Hardcoded admin password for "Simple Authentication"
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    public void execute(String[] args, GenreTree tree, ClientHandler client) {
        if (args.length < 1) {
            client.sendMessage("ERROR: Usage: LOGIN <username> [password]");
            return;
        }

        String username = args[0];

        // Check if trying to login as admin
        if ("admin".equalsIgnoreCase(username)) {
            // Check password
            if (args.length >= 2 && ADMIN_PASSWORD.equals(args[1])) {
                client.setCurrentUserId("Admin");
                client.setAdmin(true);
                client.sendMessage("SUCCESS: Logged in as Administrator.");
            } else {
                client.sendMessage("ERROR: Invalid admin password.");
            }
        } else {
            // Normal user login
            client.setCurrentUserId(username);
            client.setAdmin(false); // Reset admin rights if switching to normal user
            client.sendMessage("SUCCESS: Logged in as " + username);
        }
    }
}