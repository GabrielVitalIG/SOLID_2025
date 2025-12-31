package server.commands;

import model.GenreTree;
import server.ClientHandler;

public class HelpCommand implements Command {

    @Override
    public void execute(String[] args, GenreTree tree, ClientHandler client) {
        client.sendMessage("--- RecomTree Commands ---");
        client.sendMessage("1. ADD_MOVIE <Title> <Genre1> [Genre2] ...   (Add a new movie)");
        client.sendMessage("2. LIST_SUBTREE <GenreName>                  (List movies in a genre)");
        client.sendMessage("3. RATE_MOVIE <Title> <Rating 0-10>          (Rate a movie)");
        client.sendMessage("4. RECOMMEND [SIMILAR]                       (Get recommendations)");
        client.sendMessage("5. HELP                                      (Show this menu)");
        client.sendMessage("6. QUIT                                      (Disconnect)");
        client.sendMessage("--------------------------");
    }
}
