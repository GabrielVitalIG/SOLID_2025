package client.Export;

import java.io.IOException;

public interface ExportStrategy {
    void export(String data, String filename) throws IOException;
}
