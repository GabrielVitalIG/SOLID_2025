package client.ui;

import client.Client;
import client.ClientCommandParser;
import client.ResultExporter;
import client.Export.CsvExportStrategy;
import client.Export.JsonExportStrategy;

import java.io.IOException;
import java.util.Scanner;

public class CommandLineUI {
    private Client client;
    private Scanner scanner;

    // Variable for export purposes
    private String lastResponse = "";

    public CommandLineUI(Client client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        try {
            //Read the initial "Welcome" message from the server
            String welcome = client.receiveResponse();
            System.out.println(welcome);

            //Main Input Loop
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();

                if (input.trim().isEmpty()) continue;

                if (input.toUpperCase().startsWith("EXPORT ")) {
                    String[] parts = input.trim().split(" ");
                    if (parts.length < 3) {
                        System.out.println("Usage: EXPORT <csv|json> <filename>");
                        continue;
                    }
                    String format = parts[1];
                    String filename = parts[2];

                    if ("csv".equalsIgnoreCase(format)) {
                        ResultExporter exporter = new ResultExporter(new CsvExportStrategy());
                        exporter.exportData(lastResponse, filename);
                    } else if ("json".equalsIgnoreCase(format)) {
                        ResultExporter exporter = new ResultExporter(new JsonExportStrategy());
                        exporter.exportData(lastResponse, filename);
                    } else {
                        System.out.println("Unknown format. Use csv or json.");
                    }
                    continue;
                }
                // Validate command
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