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
        // Entry point: Connects to localhost on port 5555
        Client client = new Client("127.0.0.1", 5555);
        client.start();
    }
}