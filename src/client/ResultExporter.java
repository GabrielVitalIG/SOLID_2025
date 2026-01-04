package client;

import client.Export.ExportStrategy;
import java.io.IOException;

public class ResultExporter {
    private ExportStrategy strategy;

    public ResultExporter(ExportStrategy strategy) {
        this.strategy = strategy;
    }

    public void exportData(String data, String filename) {
        try {
            strategy.export(data, filename);
            System.out.println("Exported to " + filename + " using " + strategy.getClass().getSimpleName());
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
        }
    }
}