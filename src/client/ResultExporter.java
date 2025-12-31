package client;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ResultExporter {

    public static void exportToCsv(String data, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Simple logic: split lines, try to format as CSV
            String[] lines = data.split("\n");
            writer.println("Content"); // Header
            for (String line : lines) {
                // Escape quotes for CSV safety
                writer.println("\"" + line.replace("\"", "\"\"") + "\"");
            }
            System.out.println("Exported to " + filename);
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
        }
    }

    public static void exportToJson(String data, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("{");
            writer.println("  \"timestamp\": \"" + System.currentTimeMillis() + "\",");
            writer.println("  \"content\": [");

            String[] lines = data.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].replace("\"", "\\\"");
                writer.print("    \"" + line + "\"");
                if (i < lines.length - 1) writer.print(",");
                writer.println();
            }

            writer.println("  ]");
            writer.println("}");
            System.out.println("Exported to " + filename);
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
        }
    }
}