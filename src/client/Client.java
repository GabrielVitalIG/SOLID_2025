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

            // Initialize streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connected!");

            // Start the UI loop
            CommandLineUI ui = new CommandLineUI(this);
            ui.run();

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            close();
        }
    }

    /**
     * Sends a request to the server.
     */
    public void sendRequest(String request) {
        out.println(request);
    }

    /**
     * Reads the response from the server.
     * It reads multiple lines until it sees the "<END>" marker.
     */
    public String receiveResponse() throws IOException {
        StringBuilder response = new StringBuilder();
        String line;

        // Keep reading lines until we see the magic marker or stream ends
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Entry point: Connects to localhost on port 8888
        Client client = new Client("127.0.0.1", 8888);
        client.start();
    }
}