package client.Export;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonExportStrategy implements ExportStrategy {
    @Override
    public void export(String data, String filename) throws IOException {
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
        }
    }
}
