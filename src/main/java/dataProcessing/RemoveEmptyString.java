package dataProcessing;

import java.io.*;
import java.nio.file.*;

public class RemoveEmptyString {

    private static final String DIRECTORY = "src/main/resources/storms";

    public static void main(String[] args) throws IOException {
        File dir = new File(DIRECTORY);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));

        if (files == null || files.length == 0) {
            System.out.println("No CSV files found in " + DIRECTORY);
            return;
        }

        for (File file : files) {
            cleanFile(file.toPath());
        }
    }

    private static void cleanFile(Path path) {
        try {
            System.out.println("Cleaning: " + path);
            // Read all lines
            BufferedReader reader = Files.newBufferedReader(path);
            StringBuilder cleanedContent = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                // Split on comma
                String[] cells = line.split(",", -1); // -1 to keep trailing empty cells

                for (int i = 0; i < cells.length; i++) {
                    String trimmed = cells[i].trim();
                    if (trimmed.isEmpty()) {
                        cells[i] = "-999";
                    } else {
                        cells[i] = trimmed; // also removes spaces inside cells
                    }
                }
                // Join cells back with comma, no spaces
                cleanedContent.append(String.join(",", cells)).append("\n");
            }
            reader.close();

            // Overwrite the file with cleaned content
            Files.write(path, cleanedContent.toString().getBytes());

        } catch (IOException e) {
            System.err.println("Failed to clean file: " + path);
            e.printStackTrace();
        }
    }
}
