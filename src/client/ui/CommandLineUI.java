package client.ui;

import client.Client;
import client.ClientCommandParser; // <--- IMPORT THIS

import java.io.IOException;
import java.util.Scanner;

public class CommandLineUI {
    private Client client;
    private Scanner scanner;

    public CommandLineUI(Client client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        try {
            // 1. Read the initial "Welcome" message from the server
            String welcome = client.receiveResponse();
            System.out.println(welcome);

            // 2. Main Input Loop
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();

                if (input.trim().isEmpty()) continue;

                // >>> VALIDATION STEP <<<
                // Use the class name directly (thanks to the import above)
                if (!ClientCommandParser.validate(input)) {
                    continue; // Loop back if invalid
                }

                // Send to server if valid
                client.sendRequest(input);

                // Handle Quit
                if ("QUIT".equalsIgnoreCase(input.trim())) {
                    System.out.println("Exiting...");
                    break;
                }

                // Read and display response
                String response = client.receiveResponse();
                System.out.println(response);
            }

        } catch (IOException e) {
            System.err.println("Communication error: " + e.getMessage());
        }
    }
}