package client.Export;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CsvExportStrategy implements ExportStrategy {
    @Override
    public void export(String data, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            String[] lines = data.split("\n");
            writer.println("Content");
            for (String line : lines) {
                writer.println("\"" + line.replace("\"", "\"\"") + "\"");
            }
        }
    }
}
