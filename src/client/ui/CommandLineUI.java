package client.ui;

import client.Client;
import client.ClientCommandParser;
import client.ResultExporter;

import java.io.IOException;
import java.util.Scanner;

public class CommandLineUI {
    private Client client;
    private Scanner scanner;

    // Store the last server response for exporting
    private String lastResponse = "";

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

                // --- NEW: INTERCEPT EXPORT COMMAND ---
                // Syntax: EXPORT csv mydata.csv OR EXPORT json mydata.json
                if (input.toUpperCase().startsWith("EXPORT ")) {
                    String[] parts = input.trim().split(" ");
                    if (parts.length < 3) {
                        System.out.println("Usage: EXPORT <csv|json> <filename>");
                        continue;
                    }
                    String format = parts[1];
                    String filename = parts[2];

                    if ("csv".equalsIgnoreCase(format)) {
                        ResultExporter.exportToCsv(lastResponse, filename);
                    } else if ("json".equalsIgnoreCase(format)) {
                        ResultExporter.exportToJson(lastResponse, filename);
                    } else {
                        System.out.println("Unknown format. Use csv or json.");
                    }
                    continue; // Don't send "EXPORT" to the server!
                }
                // -------------------------------------

                // >>> VALIDATION STEP <<<
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

                // Save the response so we can export it later if the user wants
                lastResponse = response;

                System.out.println(response);
            }

        } catch (IOException e) {
            System.err.println("Communication error: " + e.getMessage());
        }
    }
}