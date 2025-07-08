package dataProcessing;

import java.io.*;
import java.util.*;

public class Pad {
    int longestSeries = 133;  // Max timesteps
    static String padLineInsert;
    public static void main(String[] args) throws Exception {
        File stormDir = new File("src/main/resources/storms");
        File[] storms = stormDir.listFiles();

        if (storms == null) {
            System.out.println("No storm files found.");
            return;
        }

        for (File storm : storms) {
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(storm));

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
            reader.close();

            int originalSize = lines.size() - 1;
            if (originalSize >= 133) {
                continue; // Already at or beyond required length
            }

            // Get column count from the first valid row
            String[] firstLineColumns = lines.get(1).split(",");
            int columnCount = firstLineColumns.length;

            padLineInsert += "-999";
            for (int i = 1; i < columnCount; i++) {
                padLineInsert += ", -999";
            }
            // Create a padded line with "-999" for each column
            StringBuilder padLine = new StringBuilder();
            for (int i = 0; i < columnCount; i++) {
                padLine.append("-999");
                if (i < columnCount - 1) padLine.append(", ");
            }

            // Add padding lines until the size matches longestSeries
            while (lines.size() < 133) {
                lines.add(padLine.toString());
            }

            // Overwrite the file with padded content
            BufferedWriter writer = new BufferedWriter(new FileWriter(storm));
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
            writer.close();

            System.out.println("Padded " + storm.getName() + " from " + originalSize + " to " + lines.size());
        }
    }
}
