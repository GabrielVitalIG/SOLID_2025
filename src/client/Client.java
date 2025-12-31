package client;

import client.ui.CommandLineUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private String host;
    private int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            System.out.println("Connecting to server at " + host + ":" + port + "...");
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connected!");
            CommandLineUI ui = new CommandLineUI(this);
            ui.run();

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            close();
        }
    }

    public void sendRequest(String request) {
        out.println(request);
    }

    public String receiveResponse() throws IOException {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            if ("<END>".equals(line)) {
                break;
            }
            response.append(line).append("\n");
        }
        return response.toString();
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        // Default mode: Interactive
        if (args.length == 0) {
            Client client = new Client("127.0.0.1", 5555);
            client.start();
        }
        // Batch Mode)
        else {
            String scriptFile = args[0];
            runBatchMode(scriptFile);
        }
    }

    private static void runBatchMode(String filename) {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filename))) {
            Client client = new Client("127.0.0.1", 5555);
            // Connect manually without UI
            System.out.println("Batch Mode: Running " + filename);

            // Initialize network
            // Note: You might need to expose the socket logic from start() or just duplicate the simple connection part here
            // Ideally, refactor 'start()' to accept an Input Source, but here is the quick way:

            java.net.Socket socket = new java.net.Socket("127.0.0.1", 5555);
            java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));

            // Read welcome message first
            readResponseLoop(in);

            String command;
            while ((command = reader.readLine()) != null) {
                if (command.trim().isEmpty()) continue;

                System.out.println(">> Executing: " + command);
                out.println(command);

                // Wait for server response
                readResponseLoop(in);

                if ("QUIT".equalsIgnoreCase(command.trim())) break;

                // Small delay to prevent flooding if necessary
                Thread.sleep(100);
            }
            socket.close();
            System.out.println("Batch execution finished.");

        } catch (Exception e) {
            System.err.println("Batch Error: " + e.getMessage());
        }
    }

    // Helper for batch mode to read until <END>
    private static void readResponseLoop(java.io.BufferedReader in) throws java.io.IOException {
        String line;
        while ((line = in.readLine()) != null) {
            if ("<END>".equals(line)) break;
            System.out.println(line);
        }
    }
}